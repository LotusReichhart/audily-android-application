package com.lotusreichhart.audily.feature.albums.impl.detail

internal sealed interface AlbumDetailUiEffect {
    data class NavigateToPlaylist(val playlistId: Long) : AlbumDetailUiEffect
}