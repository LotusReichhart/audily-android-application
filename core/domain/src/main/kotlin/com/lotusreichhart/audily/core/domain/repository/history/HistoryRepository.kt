package com.lotusreichhart.audily.core.domain.repository.history

import com.lotusreichhart.audily.core.model.history.History
import kotlinx.coroutines.flow.Flow

/**
 * Interface quản lý dữ liệu lịch sử nghe nhạc.
 */
interface HistoryRepository {
    /**
     * Cập nhật hoặc thêm mới lịch sử cho một bài hát.
     * Thường gọi khi bài hát đã được nghe đủ thời lượng (VD: > 1 phút).
     */
    suspend fun upsertHistory(songId: Long)

    /**
     * Lấy danh sách bài hát vừa nghe gần đây.
     */
    fun getRecentHistory(limit: Int): Flow<List<History>>

    /**
     * Lấy danh sách bài hát được nghe nhiều nhất.
     */
    fun getTopPlayed(limit: Int): Flow<List<History>>

    /**
     * Xóa lịch sử của một bài hát cụ thể.
     */
    suspend fun deleteHistory(songId: Long)
}