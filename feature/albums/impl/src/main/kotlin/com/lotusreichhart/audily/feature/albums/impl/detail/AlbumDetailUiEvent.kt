package com.lotusreichhart.audily.feature.albums.impl.detail

internal sealed interface AlbumDetailUiEvent {
    object PlayAll : AlbumDetailUiEvent
    object Shuffle : AlbumDetailUiEvent
    data class OnSaveAsPlaylist(val name: String, val description: String?) : AlbumDetailUiEvent
    data class SongClicked(val songId: Long, val isMissing: Boolean) : AlbumDetailUiEvent
    data class Init(val albumId: Long) : AlbumDetailUiEvent
}