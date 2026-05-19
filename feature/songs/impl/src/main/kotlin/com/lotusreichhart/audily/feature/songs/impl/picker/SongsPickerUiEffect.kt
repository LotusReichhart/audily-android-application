package com.lotusreichhart.audily.feature.songs.impl.picker

internal sealed interface SongsPickerUiEffect {
    object SongsSelectedSaved : SongsPickerUiEffect
}