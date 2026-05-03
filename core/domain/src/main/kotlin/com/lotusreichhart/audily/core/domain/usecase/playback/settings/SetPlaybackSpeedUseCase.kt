package com.lotusreichhart.audily.core.domain.usecase.playback.settings

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import javax.inject.Inject

class SetPlaybackSpeedUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(speed: Float) {
        playbackRepository.handleEvent(PlaybackEvent.SetSpeed(speed))
        preferencesRepository.updatePlaybackSpeed(speed)
    }
}
