package com.lotusreichhart.audily.core.domain.repository.song

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    /**
     * Luồng thông tin tóm tắt của danh sách bài hát (số lượng, tổng thời lượng).
     */
    fun getSongsSummary(
        searchQuery: String? = null
    ): Flow<SongsSummary>

    /**
     * Luồng tất cả ID bài hát có trên thiết bị theo tiêu chí tìm kiếm và sắp xếp.
     */
    fun getSongIds(
        searchQuery: String? = null,
        sortOrder: SongSortOrder = SongSortOrder.TITLE,
        sortType: SortOrderType = SortOrderType.ASC
    ): Flow<List<Long>>

    /**
     * Luồng dữ liệu phân trang bài hát theo tiêu chí tìm kiếm và sắp xếp.
     */
    fun getSongsPaged(
        searchQuery: String? = null,
        sortOrder: SongSortOrder = SongSortOrder.TITLE,
        sortType: SortOrderType = SortOrderType.ASC
    ): Flow<PagingData<Song>>

    /**
     * Lấy thông tin đầy đủ của một bài hát theo ID.
     */
    fun getSong(id: Long): Flow<Song?>

    /**
     * Lấy thông tin đầy đủ của một danh sách bài hát theo ID.
     */
    fun getSongs(ids: List<Long>): Flow<List<Song>>
    fun getBasicSongs(ids: List<Long>): Flow<List<Song>>
}