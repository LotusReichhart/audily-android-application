package com.lotusreichhart.audily.core.model.history

/**
 * Domain model đại diện cho lịch sử nghe nhạc của một bài hát.
 */
data class History(
    val songId: Long,
    val playCount: Int,
    val lastPlayed: Long
)
