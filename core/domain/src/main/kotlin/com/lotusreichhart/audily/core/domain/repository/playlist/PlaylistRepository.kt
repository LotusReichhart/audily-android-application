package com.lotusreichhart.audily.core.domain.repository.playlist

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    /**
     * Lấy danh sách bài hát yêu thích với hỗ trợ phân trang (dùng cho UI).
     */
    fun getPlaylists(
        searchQuery: String = "",
        sortOrder: PlaylistSortOrder = PlaylistSortOrder.CREATED_DATE,
        sortType: SortOrderType = SortOrderType.DESC
    ): Flow<List<Playlist>>

    fun getPlaylistById(id: Long): Flow<Playlist?>
    suspend fun createPlaylist(name: String, description: String? = null): Long
    suspend fun deletePlaylist(id: Long)
    suspend fun updatePlaylist(id: Long, name: String, description: String? = null)
    suspend fun addSongsToPlaylist(id: Long, songIds: List<Long>)
    suspend fun removeSongFromPlaylist(id: Long, songId: Long)
    
    fun getSongIdsInPlaylist(id: Long): Flow<List<Long>>

    /**
     * Lấy danh sách bài hát trong playlist hỗ trợ phân trang.
     * Sắp xếp theo vị trí thủ công (Drag-and-drop).
     */
    fun getPlaylistSongsPaged(id: Long): Flow<PagingData<Song>>

    /**
     * Cập nhật thứ tự bài hát trong playlist sau khi kéo thả.
     */
    suspend fun updateSongPositionsInPlaylist(id: Long, songIds: List<Long>)
}