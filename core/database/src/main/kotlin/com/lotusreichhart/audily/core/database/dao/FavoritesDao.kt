package com.lotusreichhart.audily.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lotusreichhart.audily.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Upsert
    suspend fun upsertFavorites(favorites: List<FavoriteEntity>)

    @Query("DELETE FROM favorites WHERE song_id = :songId")
    suspend fun deleteFavorite(songId: Long)

    @Query("SELECT song_id FROM favorites ORDER BY position ASC")
    fun getFavoriteIds(): Flow<List<Long>>

    @Query("SELECT * FROM favorites ORDER BY position ASC")
    fun getFavoriteEntitiesPaging(): PagingSource<Int, FavoriteEntity>

    @Query("SELECT * FROM favorites ORDER BY position ASC LIMIT :limit")
    fun getFavoriteEntities(limit: Int): Flow<List<FavoriteEntity>>

    @Query("SELECT MAX(position) FROM favorites")
    suspend fun getMaxPosition(): Int?

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE song_id = :songId)")
    fun isFavorite(songId: Long): Flow<Boolean>
}
