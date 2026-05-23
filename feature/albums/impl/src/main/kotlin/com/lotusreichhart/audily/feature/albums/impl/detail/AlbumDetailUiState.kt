package com.lotusreichhart.audily.feature.albums.impl.detail

import com.lotusreichhart.audily.core.model.album.Album
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongsSummary

internal data class AlbumDetailUiState(
    val album: Album? = null,
    val songs: List<Song> = emptyList(),
    val songsSummary: SongsSummary = SongsSummary(),
    val songIds: List<Long> = emptyList(),
    val isLoading: Boolean = true,
    val playbackState: PlaybackState = PlaybackState.INITIAL
)