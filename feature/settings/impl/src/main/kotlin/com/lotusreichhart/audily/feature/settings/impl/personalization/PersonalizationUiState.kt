package com.lotusreichhart.audily.feature.settings.impl.personalization

import com.lotusreichhart.audily.core.model.prefs.AppTheme

internal data class PersonalizationUiState(
    val appTheme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val accentColor: Int? = null,
    val dynamicColor: Boolean = false,
    val useGlassmorphism: Boolean = true
)
