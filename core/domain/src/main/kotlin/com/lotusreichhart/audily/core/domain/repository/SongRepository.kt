package com.lotusreichhart.audily.core.domain.repository

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    /**
     * Luồng tất cả ID bài hát có trên thiết bị.
     * Dùng cho logic phát nhạc, Shuffle, Queue.
     */
    fun getSongIds(): Flow<List<Long>>

    /**
     * Luồng dữ liệu phân trang bài hát.
     * Dùng cho UI danh sách (All Songs).
     */
    fun getSongsPaged(): Flow<PagingData<Song>>

    /**
     * Lấy thông tin đầy đủ của một bài hát (bao gồm cả Extended metadata) theo ID.
     */
    fun getSong(id: Long): Flow<Song?>
}