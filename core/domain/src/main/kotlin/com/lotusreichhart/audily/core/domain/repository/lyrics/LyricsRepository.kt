package com.lotusreichhart.audily.core.domain.repository.lyrics

import com.lotusreichhart.audily.core.model.lyrics.Lyrics
import kotlinx.coroutines.flow.Flow

interface LyricsRepository {
    /**
     * Lấy lời bài hát của songId dưới dạng Flow.
     */
    fun getLyrics(songId: Long): Flow<Lyrics?>

    /**
     * Tải lời bài hát từ mạng và lưu lại.
     */
    suspend fun fetchAndSaveLyrics(
        songId: Long,
        title: String,
        artist: String,
        durationMs: Long
    ): Lyrics?
}
