package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase để cập nhật tốc độ (speed) và cao độ (pitch) phát nhạc.
 */
class UpdatePlaybackParametersUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(speed: Float, pitch: Float) {
        userPreferencesRepository.updatePlaybackParameters(speed, pitch)
    }
}
