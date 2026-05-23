package com.lotusreichhart.audily.core.model.lyrics

/**
 * Một phân đoạn lời bài hát đồng bộ với thời gian.
 */
data class SyncedLyricsSegment(
    val text: String,
    val startTimeMillis: Long
)
