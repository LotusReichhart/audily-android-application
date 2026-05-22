package com.lotusreichhart.audily.core.data.repository.lyrics

import com.lotusreichhart.audily.core.database.dao.LyricsDao
import com.lotusreichhart.audily.core.database.entity.LyricsEntity
import com.lotusreichhart.audily.core.data.mapper.lyrics.toLyrics
import com.lotusreichhart.audily.core.domain.repository.lyrics.LyricsRepository
import com.lotusreichhart.audily.core.mediastore.MediaStoreLyrics
import com.lotusreichhart.audily.core.model.lyrics.Lyrics
import com.lotusreichhart.audily.core.model.lyrics.LyricsSource
import com.lotusreichhart.audily.core.network.response.LrcLibResponse
import com.lotusreichhart.audily.core.network.service.LrcLibService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class LyricsRepositoryImpl @Inject constructor(
    private val mediaStoreLyrics: MediaStoreLyrics,
    private val lyricsDao: LyricsDao,
    private val lrcLibService: LrcLibService
) : LyricsRepository {

    override fun getLyrics(songId: Long): Flow<Lyrics?> {
        return mediaStoreLyrics.getSongLyrics(songId).flatMapLatest { embedded ->
            Timber.d("getLyrics - embedded: ${embedded?.length}")
            if (!embedded.isNullOrBlank()) {
                flowOf(embedded.toLyrics(LyricsSource.EMBEDDED))
            } else {
                lyricsDao.getLyrics(songId).map { entity ->
                    Timber.d("getLyrics - entity: ${entity?.songId}")
                    entity?.toLyrics()
                }
            }
        }
    }

    private suspend fun fetchLyricsFromApiOrSearch(
        title: String,
        artist: String,
        durationSecs: Int
    ): LrcLibResponse? {
        // 1. Thử gọi API get chính xác trước
        try {
            return lrcLibService.getLyrics(
                trackName = title,
                artistName = artist,
                albumName = null,
                durationSecs = durationSecs
            )
        } catch (e: Exception) {
            val exceptionName = e.javaClass.name
            if (exceptionName.contains("HttpException")) {
                val is404 = e.message?.contains("404") == true
                if (is404) {
                    Timber.d("Exact lyrics not found on LRCLIB (404), falling back to search API...")
                } else {
                    Timber.w("HTTP error ${e.message} in exact lyrics fetch, falling back to search API...")
                }
            } else {
                Timber.w(e, "Error fetching exact lyrics, falling back to search API...")
            }
        }

        // 2. Fallback: Tìm kiếm theo tên bài hát và ca sĩ
        try {
            val searchResults = lrcLibService.searchLyrics(
                trackName = title,
                artistName = artist
            )
            if (searchResults.isNotEmpty()) {
                val resultsWithLyrics = searchResults.filter {
                    !it.syncedLyrics.isNullOrBlank() || !it.plainLyrics.isNullOrBlank()
                }
                if (resultsWithLyrics.isNotEmpty()) {
                    // Chọn kết quả có độ chênh lệch thời gian nhỏ nhất so với thời gian bài hát hiện tại
                    val bestMatch = resultsWithLyrics.minByOrNull { result ->
                        val resultDuration = result.duration ?: 0.0
                        kotlin.math.abs(resultDuration - durationSecs)
                    }
                    if (bestMatch != null) {
                        Timber.d("Found fallback lyrics via search API: ${bestMatch.trackName} by ${bestMatch.artistName} (id: ${bestMatch.id})")
                        return bestMatch
                    }
                }
            }
        } catch (searchEx: Exception) {
            Timber.e(searchEx, "LyricsRepositoryImpl - Lỗi tìm kiếm lyrics dự phòng")
        }

        return null
    }

    override suspend fun fetchAndSaveLyrics(
        songId: Long,
        title: String,
        artist: String,
        durationMs: Long
    ): Lyrics? {
        val durationSecs = (durationMs / 1000).toInt()
        try {
            val response = fetchLyricsFromApiOrSearch(title, artist, durationSecs)
            if (response == null) {
                Timber.w("fetchAndSaveLyrics - No lyrics found for songId: $songId, title: $title")
                return null
            }

            Timber.d("fetchAndSaveLyrics - response: ${response.trackName}")

            val rawContent = response.syncedLyrics ?: response.plainLyrics

            Timber.d("fetchAndSaveLyrics - rawContent: ${rawContent?.length}")
            if (rawContent.isNullOrBlank()) return null

            val isSynced = !response.syncedLyrics.isNullOrBlank()

            // Thử đồng bộ với file vật lý trước
            val syncSuccess =
                mediaStoreLyrics.updateSongLyrics(songId, rawContent).firstOrNull() ?: false

            if (syncSuccess) {
                return rawContent.toLyrics(LyricsSource.EMBEDDED)
            } else {
                // Nếu ghi file thất bại (do Scoped Storage / File Readonly), lưu vào SQLite Cache
                val entity = LyricsEntity(
                    songId = songId,
                    content = rawContent,
                    isSynced = isSynced,
                    source = LyricsSource.REMOTE.name
                )
                lyricsDao.insertLyrics(entity)
                return rawContent.toLyrics(LyricsSource.REMOTE)
            }
        } catch (e: Exception) {
            Timber.e(
                e,
                "LyricsRepositoryImpl - Lỗi tải/lưu lyrics cho songId: $songId, title: $title"
            )
            return null
        }
    }
}
