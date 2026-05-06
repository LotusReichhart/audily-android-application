package com.lotusreichhart.audily.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lotusreichhart.audily.core.database.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Upsert
    suspend fun upsertHistory(history: HistoryEntity)

    @Query("SELECT * FROM playback_history ORDER BY last_played DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM playback_history ORDER BY play_count DESC LIMIT :limit")
    fun getTopPlayed(limit: Int): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM playback_history WHERE song_id = :songId")
    suspend fun getHistoryById(songId: Long): HistoryEntity?

    @Query("DELETE FROM playback_history WHERE song_id = :songId")
    suspend fun deleteHistory(songId: Long)
}
