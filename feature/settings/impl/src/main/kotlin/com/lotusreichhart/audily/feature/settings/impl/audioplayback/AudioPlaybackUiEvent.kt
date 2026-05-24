package com.lotusreichhart.audily.feature.settings.impl.audioplayback

internal sealed interface AudioPlaybackUiEvent {
    data class OnAutoplayOnHeadphoneConnectChanged(val enabled: Boolean) : AudioPlaybackUiEvent
    data class OnAutoplayOnBluetoothConnectChanged(val enabled: Boolean) : AudioPlaybackUiEvent
    data class OnAudioDuckingChanged(val enabled: Boolean) : AudioPlaybackUiEvent
    object OnOpenEqualizerFailed : AudioPlaybackUiEvent
}