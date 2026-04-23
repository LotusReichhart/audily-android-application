package com.lotusreichhart.audily.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.lotusreichhart.audily.core.database.entity.PlaylistEntity

data class PlaylistWithCount(
    @Embedded val playlist: PlaylistEntity,
    @ColumnInfo(name = "song_count") val songCount: Int
)