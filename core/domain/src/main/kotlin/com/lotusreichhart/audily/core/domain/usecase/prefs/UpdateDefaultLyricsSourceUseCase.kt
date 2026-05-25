package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.prefs.LyricsProvider
import javax.inject.Inject

class UpdateDefaultLyricsSourceUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(source: LyricsProvider) = repository.updateDefaultLyricsSource(source)
}
