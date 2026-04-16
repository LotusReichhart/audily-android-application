package com.lotusreichhart.audily.core.mediastore.model

data class MediaStoreSong(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val data: String,
    val albumId: Long,
    val dateModified: Long
)
