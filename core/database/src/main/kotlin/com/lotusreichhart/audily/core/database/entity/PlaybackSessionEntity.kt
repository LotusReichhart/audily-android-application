package com.lotusreichhart.audily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Thực thể lưu trữ trạng thái phiên phát nhạc hiện tại (Session).
 * Luôn chỉ có duy nhất 1 hàng với id = 1.
 */
@Entity(tableName = "playback_session")
data class PlaybackSessionEntity(
    @PrimaryKey
    val id: Int = 1,
    val currentSongId: Long?,
    val position: Long,
    val duration: Long,
    val sourceId: Long?,
    val sourceType: String?
)
