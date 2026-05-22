package com.lotusreichhart.audily.core.model.lyrics

/**
 * Model cho lời bài hát không đồng bộ (chỉ là văn bản thuần túy).
 */
data class PlainLyrics(
    val content: String,
    val source: LyricsSource
)
