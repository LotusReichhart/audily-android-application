package com.lotusreichhart.audily.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Thực thể lưu trữ lịch sử nghe nhạc trong Room Database.
 */
@Entity(tableName = "playback_history")
data class HistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "song_id")
    val songId: Long,
    
    @ColumnInfo(name = "play_count")
    val playCount: Int,
    
    @ColumnInfo(name = "last_played")
    val lastPlayed: Long
)
