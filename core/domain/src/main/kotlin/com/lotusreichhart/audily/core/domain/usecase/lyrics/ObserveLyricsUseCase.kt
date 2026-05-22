package com.lotusreichhart.audily.core.domain.usecase.lyrics

import com.lotusreichhart.audily.core.domain.repository.lyrics.LyricsRepository
import com.lotusreichhart.audily.core.model.lyrics.Lyrics
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    operator fun invoke(songId: Long): Flow<Lyrics?> {
        return lyricsRepository.getLyrics(songId)
    }
}
