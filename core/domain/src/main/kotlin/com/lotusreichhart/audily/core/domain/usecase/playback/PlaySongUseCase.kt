package com.lotusreichhart.audily.core.domain.usecase.playback

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.song.Song
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    suspend operator fun invoke(song: Song) = playbackRepository.play(song)
}