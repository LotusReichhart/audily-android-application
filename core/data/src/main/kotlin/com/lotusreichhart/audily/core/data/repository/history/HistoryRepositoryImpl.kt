package com.lotusreichhart.audily.core.data.repository.history

import com.lotusreichhart.audily.core.data.mapper.history.toHistory
import com.lotusreichhart.audily.core.database.dao.HistoryDao
import com.lotusreichhart.audily.core.database.entity.HistoryEntity
import com.lotusreichhart.audily.core.domain.repository.history.HistoryRepository
import com.lotusreichhart.audily.core.model.history.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Triển khai HistoryRepository sử dụng Room Database làm nguồn dữ liệu.
 */
class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override suspend fun upsertHistory(songId: Long) {
        val existing = historyDao.getHistoryById(songId)
        val newHistory = existing?.copy(
            playCount = existing.playCount + 1,
            lastPlayed = System.currentTimeMillis()
        )
            ?: HistoryEntity(
                songId = songId,
                playCount = 1,
                lastPlayed = System.currentTimeMillis()
            )
        historyDao.upsertHistory(newHistory)
    }

    override fun getRecentHistory(limit: Int): Flow<List<History>> {
        return historyDao.getRecentHistory(limit).map { entities ->
            entities.map { it.toHistory() }
        }
    }

    override fun getTopPlayed(limit: Int): Flow<List<History>> {
        return historyDao.getTopPlayed(limit).map { entities ->
            entities.map { it.toHistory() }
        }
    }

    override suspend fun deleteHistory(songId: Long) {
        historyDao.deleteHistory(songId)
    }

    override suspend fun clearHistory() {
        historyDao.clearHistory()
    }
}

