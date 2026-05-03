package com.lotusreichhart.audily.core.domain.usecase.playback.settings

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import javax.inject.Inject

class SetPlaybackPitchUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(pitch: Float) {
        playbackRepository.handleEvent(PlaybackEvent.SetPitch(pitch))
        preferencesRepository.updatePlaybackPitch(pitch)
    }
}
