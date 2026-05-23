package com.lotusreichhart.audily.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lotusreichhart.audily.core.database.entity.PlaybackSessionEntity
import com.lotusreichhart.audily.core.database.entity.PlayingQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackDao {

    // === Playback Session ===

    @Query("SELECT * FROM playback_session WHERE id = 1")
    fun getSession(): Flow<PlaybackSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: PlaybackSessionEntity)

    @Query("DELETE FROM playback_session")
    suspend fun clearSession()

    // === Playing Queue ===

    @Query("SELECT * FROM playing_queue ORDER BY orderIndex ASC")
    fun getQueue(): Flow<List<PlayingQueueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItems(items: List<PlayingQueueEntity>)

    @Query("DELETE FROM playing_queue")
    suspend fun clearQueue()

    // === Atomic Operations ===

    @Transaction
    suspend fun saveFullPlaybackState(
        session: PlaybackSessionEntity,
        queueItems: List<PlayingQueueEntity>
    ) {
        saveSession(session)
        clearQueue()
        insertQueueItems(queueItems)
    }
}
