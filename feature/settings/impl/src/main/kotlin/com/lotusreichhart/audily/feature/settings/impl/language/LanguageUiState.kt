package com.lotusreichhart.audily.feature.settings.impl.language

import com.lotusreichhart.audily.core.model.prefs.AppLanguage

internal data class LanguageUiState(
    val currentLanguage: AppLanguage = AppLanguage.FOLLOW_SYSTEM
)
