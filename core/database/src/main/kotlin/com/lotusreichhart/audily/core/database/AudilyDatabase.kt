package com.lotusreichhart.audily.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lotusreichhart.audily.core.database.dao.FavoritesDao
import com.lotusreichhart.audily.core.database.dao.PlaylistDao
import com.lotusreichhart.audily.core.database.entity.FavoriteEntity
import com.lotusreichhart.audily.core.database.entity.PlaylistEntity
import com.lotusreichhart.audily.core.database.entity.PlaylistSongCrossRef

@Database(
    entities = [
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AudilyDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistDao(): PlaylistDao
}