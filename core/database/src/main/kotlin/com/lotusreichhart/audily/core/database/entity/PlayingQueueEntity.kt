package com.lotusreichhart.audily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Thực thể lưu trữ danh sách bài hát trong hàng đợi đang phát.
 */
@Entity(tableName = "playing_queue")
data class PlayingQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val songId: Long,
    val orderIndex: Int
)
