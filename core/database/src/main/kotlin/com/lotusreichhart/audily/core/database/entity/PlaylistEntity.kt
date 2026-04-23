package com.lotusreichhart.audily.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "image_uri")
    val imageUri: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)
