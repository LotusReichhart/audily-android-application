package com.lotusreichhart.audily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lyrics")
data class LyricsEntity(
    @PrimaryKey val songId: Long,
    val content: String,
    val isSynced: Boolean,
    val source: String
)
