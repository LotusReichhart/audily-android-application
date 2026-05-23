package com.lotusreichhart.audily.feature.playlists.impl

internal sealed interface PlaylistsUiEffect {
    object PlaylistCreated : PlaylistsUiEffect
}