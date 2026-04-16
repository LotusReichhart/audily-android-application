package com.lotusreichhart.audily.core.model.album

/**
 * Thông tin chi tiết hơn về Album.
 */
data class AlbumMetadata(
    val albumId: Long,
    val year: Int? = null,
    val genre: String? = null,
    val totalDuration: Long = 0,
    val description: String? = null
)
