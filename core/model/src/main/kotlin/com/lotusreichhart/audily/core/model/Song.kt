package com.lotusreichhart.audily.core.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val path: String,
    val trackNumber: Int,
    val discNumber: Int?,
    val year: Int,
    val addedAt: Long,
    val isFavorite: Boolean
)