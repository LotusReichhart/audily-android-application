package com.lotusreichhart.audily.core.model.song

data class BasicSongMetadata(
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val path: String
)
