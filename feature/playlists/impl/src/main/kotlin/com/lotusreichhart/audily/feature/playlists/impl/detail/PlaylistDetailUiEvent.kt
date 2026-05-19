package com.lotusreichhart.audily.feature.playlists.impl.detail

/**
 * Các sự kiện người dùng tại màn hình chi tiết Playlist.
 */
internal sealed interface PlaylistDetailUiEvent {
    data class EditMetadata(val name: String, val description: String?) : PlaylistDetailUiEvent
    object DeletePlaylist : PlaylistDetailUiEvent
    data class RemoveSong(val songId: Long) : PlaylistDetailUiEvent
    data class ReorderSongs(val fromIndex: Int, val toIndex: Int) : PlaylistDetailUiEvent
    object PlayAll : PlaylistDetailUiEvent
    data class SongClicked(val songId: Long, val isMissing: Boolean) : PlaylistDetailUiEvent
    data class Init(val playlistId: Long) : PlaylistDetailUiEvent
}
