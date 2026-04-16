package com.lotusreichhart.audily.core.model.lyrics

/**
 * Các loại nguồn lấy lời bài hát.
 */
enum class LyricsSource {
    EMBEDDED,    // Lấy từ metadata của file nhạc
    REMOTE,      // Lấy từ server hoặc Internet
    LOCAL_FILE   // Lấy từ file .lrc hoặc .txt riêng biệt
}

/**
 * Model cho lời bài hát không đồng bộ (chỉ là văn bản thuần túy).
 */
data class PlainLyrics(
    val content: String,
    val source: LyricsSource
)

/**
 * Một phân đoạn lời bài hát đồng bộ với thời gian.
 */
data class SyncedLyricsSegment(
    val text: String,
    val startTimeMillis: Long
)

/**
 * Model cho lời bài hát đồng bộ (LRC).
 */
data class SyncedLyrics(
    val segments: List<SyncedLyricsSegment>,
    val source: LyricsSource
)
