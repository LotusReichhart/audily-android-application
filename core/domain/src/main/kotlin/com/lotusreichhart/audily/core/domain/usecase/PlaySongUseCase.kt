package com.lotusreichhart.audily.core.domain.usecase

import com.lotusreichhart.audily.core.domain.repository.MediaPlayerRepository
import com.lotusreichhart.audily.core.model.song.Song
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(
    private val mediaPlayerRepository: MediaPlayerRepository
) {
    suspend operator fun invoke(song: Song) = mediaPlayerRepository.play(song)
}