package com.lotusreichhart.audily.core.model.song

/**
 * Metadata mở rộng cho bài hát, được tải khi cần xem chi tiết hoặc phát nhạc.
 */
data class ExtendedSongMetadata(
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val year: Int? = null,
    val genre: String? = null,
    val composer: String? = null,
    val fileSize: Long = 0,
    val addedAt: Long = 0,
    val lyrics: String? = null
)
