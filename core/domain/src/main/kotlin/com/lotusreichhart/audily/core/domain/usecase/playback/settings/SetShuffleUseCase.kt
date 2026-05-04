package com.lotusreichhart.audily.core.domain.usecase.playback.settings

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import javax.inject.Inject

class SetShuffleUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        playbackRepository.handleEvent(PlaybackEvent.SetShuffle(enabled))
        preferencesRepository.updateShuffleEnabled(enabled)
    }
}
