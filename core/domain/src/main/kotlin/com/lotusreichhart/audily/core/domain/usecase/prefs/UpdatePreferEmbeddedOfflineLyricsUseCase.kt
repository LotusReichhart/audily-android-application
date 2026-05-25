package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import javax.inject.Inject

class UpdatePreferEmbeddedOfflineLyricsUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(prefer: Boolean) = repository.updatePreferEmbeddedOfflineLyrics(prefer)
}
