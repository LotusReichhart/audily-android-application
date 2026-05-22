package com.lotusreichhart.audily.core.model.lyrics

/**
 * Model cho lời bài hát đồng bộ (LRC).
 */
data class SyncedLyrics(
    val segments: List<SyncedLyricsSegment>,
    val source: LyricsSource
)
