package com.lotusreichhart.audily.core.domain.repository.favorite

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun isFavorite(id: Long): Flow<Boolean>
    suspend fun toggleFavorite(id: Long)
    
    /**
     * Lấy danh sách bài hát yêu thích với hỗ trợ phân trang (dùng cho UI).
     */
    fun getFavoriteSongsPaged(): Flow<PagingData<Song>>

    /**
     * Lấy toàn bộ danh sách ID yêu thích (dùng cho logic phát nhạc/Shuffle).
     */
    fun getFavoriteIds(): Flow<List<Long>>

    /**
     * Lấy danh sách giới hạn bài hát yêu thích (dùng cho Preview/Artwork).
     */
    fun getFavoriteSongsSummary(limit: Int): Flow<List<Song>>

    /**
     * Xóa toàn bộ danh sách bài hát yêu thích.
     */
    suspend fun clearFavorites()
}