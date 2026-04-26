package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.prefs.NowPlayingTheme
import javax.inject.Inject

class UpdateNowPlayingThemeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(theme: NowPlayingTheme) = repository.updateNowPlayingTheme(theme)
}
