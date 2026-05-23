package com.lotusreichhart.audily.core.model.playlist

/**
 * Model đại diện cho một Playlist.
 *
 * @property id ID duy nhất của playlist.
 * @property name Tên của playlist.
 * @property description Mô tả về playlist (có thể null).
 * @property songCount Số lượng bài hát trong playlist.
 * @property artworkUris Danh sách các URI hình ảnh (tối đa 4) dùng để hiển thị ảnh bìa dạng lưới.
 * @property createdAt Thời điểm tạo playlist.
 */
data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val songCount: Int,
    val artworkUris: List<String?> = emptyList(),
    val createdAt: Long
)
