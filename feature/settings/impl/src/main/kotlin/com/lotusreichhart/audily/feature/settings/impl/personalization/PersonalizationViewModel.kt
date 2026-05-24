package com.lotusreichhart.audily.feature.settings.impl.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAccentColorUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAppThemeUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateDynamicColorUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateUseGlassmorphismUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PersonalizationViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateAppTheme: UpdateAppThemeUseCase,
    private val updateAccentColor: UpdateAccentColorUseCase,
    private val updateDynamicColor: UpdateDynamicColorUseCase,
    private val updateUseGlassmorphism: UpdateUseGlassmorphismUseCase
) : ViewModel() {

    val uiState: StateFlow<PersonalizationUiState> = getUserPreferencesUseCase()
        .map { prefs ->
            PersonalizationUiState(
                appTheme = prefs.uiSettings.appTheme,
                accentColor = prefs.uiSettings.accentColor,
                dynamicColor = prefs.uiSettings.dynamicColor,
                useGlassmorphism = prefs.uiSettings.useGlassmorphism
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PersonalizationUiState()
        )

    fun onEvent(event: PersonalizationUiEvent) {
        viewModelScope.launch {
            when (event) {
                is PersonalizationUiEvent.OnThemeSelected -> {
                    updateAppTheme(event.theme)
                }
                is PersonalizationUiEvent.OnAccentColorSelected -> {
                    updateAccentColor(event.color)
                }
                is PersonalizationUiEvent.OnDynamicColorToggled -> {
                    updateDynamicColor(event.enabled)
                }
                is PersonalizationUiEvent.OnGlassmorphismToggled -> {
                    updateUseGlassmorphism(event.enabled)
                }
            }
        }
    }
}