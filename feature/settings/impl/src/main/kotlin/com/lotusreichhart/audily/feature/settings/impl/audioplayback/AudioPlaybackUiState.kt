package com.lotusreichhart.audily.feature.settings.impl.audioplayback

internal data class AudioPlaybackUiState(
    val autoplayOnHeadphoneConnect: Boolean = false,
    val autoplayOnBluetoothConnect: Boolean = false,
    val audioDucking: Boolean = true
)
