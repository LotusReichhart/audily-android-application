package com.lotusreichhart.audily.feature.playlists.impl.picker

internal sealed interface PlaylistsPickerUiEffect {
    object PlaylistsSelectedSaved : PlaylistsPickerUiEffect
    object PlaylistCreated : PlaylistsPickerUiEffect
}