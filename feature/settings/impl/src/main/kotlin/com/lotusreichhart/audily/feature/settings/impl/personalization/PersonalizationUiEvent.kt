package com.lotusreichhart.audily.feature.settings.impl.personalization

import com.lotusreichhart.audily.core.model.prefs.AppTheme

internal sealed interface PersonalizationUiEvent {
    data class OnThemeSelected(val theme: AppTheme) : PersonalizationUiEvent
    data class OnAccentColorSelected(val color: Int?) : PersonalizationUiEvent
    data class OnDynamicColorToggled(val enabled: Boolean) : PersonalizationUiEvent
    data class OnGlassmorphismToggled(val enabled: Boolean) : PersonalizationUiEvent
}