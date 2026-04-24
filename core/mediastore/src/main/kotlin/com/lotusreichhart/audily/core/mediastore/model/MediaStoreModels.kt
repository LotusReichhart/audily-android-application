package com.lotusreichhart.audily.core.mediastore.model

/**
 * Dữ liệu bài hát cơ bản từ MediaStore (dùng cho danh sách).
 */
data class BasicMediaStoreMetadata(
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val path: String,
    val dateModified: Long,
    val artworkUri: String? = null
)

/**
 * Dữ liệu bài hát mở rộng từ MediaStore (dùng cho chi tiết).
 */
data class ExtendedMediaStoreMetadata(
    val track: Int? = null,
    val year: Int? = null,
    val size: Long = 0,
    val composer: String? = null
)

/**
 * Model bài hát tại tầng MediaStore (Data Layer).
 */
data class MediaStoreSong(
    val id: Long,
    val basic: BasicMediaStoreMetadata,
    val extended: ExtendedMediaStoreMetadata? = null
)

/**
 * Dữ liệu cực nhẹ chỉ phục vụ mục đích sorting trong bộ nhớ.
 */
data class MediaStoreSortMetadata(
    val id: Long,
    val title: String,
    val artist: String,
    val dateModified: Long
)

/**
 * Dữ liệu tóm tắt thô từ MediaStore.
 */
data class MediaStoreSongsSummary(
    val count: Int,
    val totalDuration: Long
)
