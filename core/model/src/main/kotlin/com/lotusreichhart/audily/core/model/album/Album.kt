package com.lotusreichhart.audily.core.model.album

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val albumArtUri: String,
    val songCount: Int
)
