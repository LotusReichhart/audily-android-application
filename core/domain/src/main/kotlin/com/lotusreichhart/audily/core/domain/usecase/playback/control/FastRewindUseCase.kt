package com.lotusreichhart.audily.core.domain.usecase.playback.control

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * UseCase thực hiện việc tua lại theo khoảng cách (skip duration) trong settings.
 */
class FastRewindUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        val skipDuration = userPreferencesRepository.getUserPreferences()
            .first().playbackSettings.skipDuration.toLong()

        playbackRepository.handleEvent(PlaybackEvent.SeekBy(-skipDuration * 1000))
    }
}
