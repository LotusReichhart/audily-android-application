package com.lotusreichhart.audily.feature.playlists.impl.detail

import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongsSummary

/**
 * Trạng thái giao diện cho màn hình chi tiết Playlist.
 */
data class PlaylistDetailUiState(
    val playlist: Playlist? = null,
    val songs: List<Song> = emptyList(),
    val songsSummary: SongsSummary = SongsSummary(),
    val songIds: List<Long> = emptyList(),
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val playbackState: PlaybackState = PlaybackState.INITIAL
)
