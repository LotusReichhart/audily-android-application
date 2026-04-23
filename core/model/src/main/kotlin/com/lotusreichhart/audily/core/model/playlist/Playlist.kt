package com.lotusreichhart.audily.core.model.playlist

data class Playlist(
    val id: Long,
    val name: String,
    val imageUri: String? = null,
    val songCount: Int,
    val createdAt: Long
)
