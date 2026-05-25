package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.prefs.AppLanguage
import javax.inject.Inject

class UpdateAppLanguageUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(language: AppLanguage) = repository.updateAppLanguage(language)
}
