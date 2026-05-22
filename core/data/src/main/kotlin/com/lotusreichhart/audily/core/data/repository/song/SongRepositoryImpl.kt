package com.lotusreichhart.audily.core.data.repository.song

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.lotusreichhart.audily.core.data.mapper.song.toSong
import com.lotusreichhart.audily.core.data.mapper.song.toSongsSummary
import com.lotusreichhart.audily.core.data.util.SongSorter
import com.lotusreichhart.audily.core.data.paging.AudilyPagingConfig
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.mediastore.MediaStoreIdPagingSource
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.RingtoneResult
import com.lotusreichhart.audily.core.model.song.DeleteSongResult
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class SongRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val mediaStoreDataSource: MediaStoreDataSource,
) : SongRepository {

    override fun getSongsSummary(searchQuery: String?): Flow<SongsSummary> {
        return mediaStoreDataSource.getSongsSummary(searchQuery).map { it.toSongsSummary() }
    }

    override fun getSongIds(
        searchQuery: String?,
        sortOrder: SongSortOrder,
        sortType: SortOrderType
    ): Flow<List<Long>> {
        return mediaStoreDataSource.getSongsSortMetadata(searchQuery).map { metadataList ->
            SongSorter.sort(metadataList, sortOrder, sortType).map { it.id }
        }
    }

    override fun getSongsPaged(
        searchQuery: String?,
        sortOrder: SongSortOrder,
        sortType: SortOrderType
    ): Flow<PagingData<Song>> {
        return mediaStoreDataSource.getSongsSortMetadata(searchQuery)
            .flatMapLatest { metadataList ->
                val sortedIds = SongSorter.sort(metadataList, sortOrder, sortType).map { it.id }

                Pager(
                    config = AudilyPagingConfig.defaultConfig(),
                    pagingSourceFactory = {
                        MediaStoreIdPagingSource(
                            dataSources = mediaStoreDataSource,
                            sortedIds = sortedIds
                        )
                    }
                ).flow.map { pagingData ->
                    pagingData.map { it.toSong() }
                }
            }
    }

    override fun getSong(id: Long): Flow<Song?> {
        return mediaStoreDataSource.observeMusicUri().map {
            mediaStoreDataSource.getSong(id)?.toSong()
        }
    }

    override fun getSongsByIds(ids: List<Long>): Flow<List<Song>> {
        return mediaStoreDataSource.observeMusicUri().map {
            val songs = mediaStoreDataSource.getSongs(ids).map { it.toSong() }
            val songsMap = songs.associateBy { it.id }
            ids.map { id ->
                songsMap[id] ?: Song(
                    id = id,
                    basic = BasicSongMetadata.EMPTY,
                    isMissing = true
                )
            }
        }
    }

    override fun getBasicSong(id: Long): Flow<Song?> {
        return mediaStoreDataSource.observeMusicUri().map {
            mediaStoreDataSource.getBasicSong(id)?.toSong()
        }
    }

    override fun getBasicSongs(ids: List<Long>): Flow<List<Song>> {
        return mediaStoreDataSource.observeMusicUri().map {
            val songs = mediaStoreDataSource.getBasicSongs(ids).map { it.toSong() }
            val songsMap = songs.associateBy { it.id }
            ids.map { id ->
                songsMap[id] ?: Song(
                    id = id,
                    basic = BasicSongMetadata.EMPTY,
                    isMissing = true
                )
            }
        }
    }

    override suspend fun setAsRingtone(id: Long): RingtoneResult {
        // 1. Kiểm tra quyền WRITE_SETTINGS
        if (!Settings.System.canWrite(context)) {
            Timber.d("SongRepositoryImpl - NO_PERMISSION")
            return RingtoneResult.NO_PERMISSION
        }

        return try {
            val contentResolver = context.contentResolver
            val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

            // 2. Cập nhật MediaStore (Đánh dấu là nhạc chuông)
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.IS_RINGTONE, true)
                put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
                put(MediaStore.Audio.Media.IS_ALARM, false)
                put(MediaStore.Audio.Media.IS_MUSIC, true)
            }
            contentResolver.update(uri, values, null, null)

            // 3. Đặt làm nhạc chuông mặc định
            RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE,
                uri
            )

            Timber.d("SongRepositoryImpl - Successfully set ringtone for id: $id")
            RingtoneResult.SUCCESS
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
                return RingtoneResult.NEED_SCOPED_STORAGE_PERMISSION(e.userAction.actionIntent.intentSender)
            }
            Timber.e(e, "SongRepositoryImpl - Failed to set ringtone for id: $id")
            RingtoneResult.FAILED
        }
    }

    override suspend fun deleteSong(id: Long): DeleteSongResult {
        return try {
            mediaStoreDataSource.deleteSong(id)
            Timber.d("SongRepositoryImpl - Successfully deleted song for id: $id")
            DeleteSongResult.SUCCESS
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
                DeleteSongResult.NEED_SCOPED_STORAGE_PERMISSION(e.userAction.actionIntent.intentSender)
            } else {
                Timber.e(e, "SongRepositoryImpl - Failed to delete song for id: $id")
                DeleteSongResult.FAILED
            }
        }
    }
}