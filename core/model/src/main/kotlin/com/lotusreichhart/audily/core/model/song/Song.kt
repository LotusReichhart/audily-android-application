package com.lotusreichhart.audily.core.model.song

/**
 * Dữ liệu mở rộng [extended] có thể null nếu chưa được tải (Lazy loading).
 */
data class Song(
    val id: Long,
    val basic: BasicSongMetadata,
    val extended: ExtendedSongMetadata? = null,
    val isFavorite: Boolean = false,
    val isMissing: Boolean = false,
    val position: Int? = null
)
