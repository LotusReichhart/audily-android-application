package com.lotusreichhart.audily.core.domain.usecase.playback.settings

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import javax.inject.Inject

class SetRepeatModeUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(mode: RepeatMode) {
        playbackRepository.handleEvent(PlaybackEvent.SetRepeatMode(mode))
        preferencesRepository.updateRepeatMode(mode)
    }
}
