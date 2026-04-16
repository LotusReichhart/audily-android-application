package com.lotusreichhart.audily.core.model

data class PlaylistWithSongs(
    val playlist: Playlist,
    val songs: List<Song>
)
