package com.lotusreichhart.audily.core.model

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val year: Int,
    val songCount: Int,
    val albumArtUri: String
)
