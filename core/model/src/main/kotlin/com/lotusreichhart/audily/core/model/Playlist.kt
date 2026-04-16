package com.lotusreichhart.audily.core.model

data class Playlist(
    val id: Long,
    val title: String,
    val description: String?,
    val songCount: Int,
    val createdAt: Long
)
