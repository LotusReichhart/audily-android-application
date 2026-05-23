package com.lotusreichhart.audily.core.model.playlist

import com.lotusreichhart.audily.core.model.song.Song

data class PlaylistWithSongs(
    val playlist: Playlist,
    val songs: List<Song>
)
