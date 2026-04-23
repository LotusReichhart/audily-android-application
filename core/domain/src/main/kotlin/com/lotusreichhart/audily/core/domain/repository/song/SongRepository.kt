package com.lotusreichhart.audily.core.domain.repository.song

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    /**
     * Luồng tất cả ID bài hát có trên thiết bị theo tiêu chí tìm kiếm và sắp xếp.
     */
    fun getSongIds(
        searchQuery: String? = null,
        sortOrder: SongSortOrder = SongSortOrder.TITLE_ASC
    ): Flow<List<Long>>

    /**
     * Luồng dữ liệu phân trang bài hát theo tiêu chí tìm kiếm và sắp xếp.
     */
    fun getSongsPaged(
        searchQuery: String? = null,
        sortOrder: SongSortOrder = SongSortOrder.TITLE_ASC
    ): Flow<PagingData<Song>>

    /**
     * Lấy thông tin đầy đủ của một bài hát theo ID.
     */
    fun getSong(id: Long): Flow<Song?>
}