package com.lotusreichhart.audily.feature.settings.impl.language

import com.lotusreichhart.audily.core.model.prefs.AppLanguage

internal sealed interface LanguageUiEvent {
    data class OnLanguageSelected(val language: AppLanguage) : LanguageUiEvent
}