package com.lotusreichhart.audily.core.domain.usecase.playback.settings

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import javax.inject.Inject

class SetPlaybackSpeedUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    suspend operator fun invoke(speed: Float) = playbackRepository.handleEvent(PlaybackEvent.SetSpeed(speed))
}
