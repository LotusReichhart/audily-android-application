package com.lotusreichhart.audily.core.domain.usecase.lyrics

import com.lotusreichhart.audily.core.domain.repository.lyrics.LyricsRepository
import com.lotusreichhart.audily.core.model.lyrics.Lyrics
import javax.inject.Inject

class FetchAndSaveLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    suspend operator fun invoke(
        songId: Long,
        title: String,
        artist: String,
        durationMs: Long
    ): Lyrics? {
        return lyricsRepository.fetchAndSaveLyrics(songId, title, artist, durationMs)
    }
}
