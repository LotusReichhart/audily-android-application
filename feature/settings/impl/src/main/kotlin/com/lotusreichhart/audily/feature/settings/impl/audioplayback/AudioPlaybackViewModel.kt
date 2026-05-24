package com.lotusreichhart.audily.feature.settings.impl.audioplayback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAudioDuckingUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAutoplayOnBluetoothConnectUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAutoplayOnHeadphoneConnectUseCase
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import com.lotusreichhart.audily.feature.settings.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AudioPlaybackViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateAutoplayOnHeadphoneConnect: UpdateAutoplayOnHeadphoneConnectUseCase,
    private val updateAutoplayOnBluetoothConnect: UpdateAutoplayOnBluetoothConnectUseCase,
    private val updateAudioDucking: UpdateAudioDuckingUseCase,
    private val globalUiEventBus: GlobalUiEventBus
) : ViewModel() {

    val uiState: StateFlow<AudioPlaybackUiState> = getUserPreferencesUseCase()
        .map { prefs ->
            AudioPlaybackUiState(
                autoplayOnHeadphoneConnect = prefs.playbackSettings.autoplayOnHeadphoneConnect,
                autoplayOnBluetoothConnect = prefs.playbackSettings.autoplayOnBluetoothConnect,
                audioDucking = prefs.playbackSettings.audioDucking
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AudioPlaybackUiState()
        )

    fun onEvent(event: AudioPlaybackUiEvent) {
        viewModelScope.launch {
            when (event) {
                is AudioPlaybackUiEvent.OnAutoplayOnHeadphoneConnectChanged -> {
                    updateAutoplayOnHeadphoneConnect(event.enabled)
                }
                is AudioPlaybackUiEvent.OnAutoplayOnBluetoothConnectChanged -> {
                    updateAutoplayOnBluetoothConnect(event.enabled)
                }
                is AudioPlaybackUiEvent.OnAudioDuckingChanged -> {
                    updateAudioDucking(event.enabled)
                }
                is AudioPlaybackUiEvent.OnOpenEqualizerFailed -> {
                    globalUiEventBus.emit(
                        GlobalUiEvent.ShowSnackbar(
                            message = UiText.StringResource(R.string.feature_settings_impl_audio_equalizer_unsupported)
                        )
                    )
                }
            }
        }
    }
}