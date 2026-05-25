package com.lotusreichhart.audily.feature.settings.impl.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAppLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LanguageViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateAppLanguageUseCase: UpdateAppLanguageUseCase
) : ViewModel() {

    val uiState: StateFlow<LanguageUiState> = getUserPreferencesUseCase()
        .map { LanguageUiState(currentLanguage = it.uiSettings.appLanguage) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LanguageUiState()
        )

    fun onEvent(event: LanguageUiEvent) {
        viewModelScope.launch {
            when (event) {
                is LanguageUiEvent.OnLanguageSelected -> {
                    updateAppLanguageUseCase(event.language)
                }
            }
        }
    }
}