package com.lotusreichhart.audily.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lotusreichhart.audily.core.database.dao.FavoritesDao
import com.lotusreichhart.audily.core.database.dao.PlaybackDao
import com.lotusreichhart.audily.core.database.dao.PlaylistDao
import com.lotusreichhart.audily.core.database.entity.FavoriteEntity
import com.lotusreichhart.audily.core.database.entity.PlaybackSessionEntity
import com.lotusreichhart.audily.core.database.entity.PlaylistEntity
import com.lotusreichhart.audily.core.database.entity.PlaylistSongCrossRef
import com.lotusreichhart.audily.core.database.entity.PlayingQueueEntity
import com.lotusreichhart.audily.core.database.entity.HistoryEntity
import com.lotusreichhart.audily.core.database.dao.HistoryDao

@Database(
    entities = [
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        PlaybackSessionEntity::class,
        PlayingQueueEntity::class,
        HistoryEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
abstract class AudilyDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playbackDao(): PlaybackDao
    abstract fun historyDao(): HistoryDao
}