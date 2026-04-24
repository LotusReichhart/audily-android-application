package com.lotusreichhart.audily.core.model.song

/**
 * Tóm tắt thông tin của danh sách bài hát (ví dụ: toàn bộ thư viện hoặc kết quả tìm kiếm).
 */
data class SongsSummary(
    val totalCount: Int = 0,
    val totalDuration: Long = 0L // Tổng thời lượng tính bằng milliseconds
)
