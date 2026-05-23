package com.lotusreichhart.audily.core.model.album

import com.lotusreichhart.audily.core.model.song.Song

data class AlbumWithSongs(
    val album: Album,
    val songs: List<Song>
)
