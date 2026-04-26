package com.lotusreichhart.audily.core.domain.repository.album

import com.lotusreichhart.audily.core.model.album.Album
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    /**
     * Lấy danh sách Album (Basic info)
     */
    fun getAlbums(
        searchQuery: String = "",
        sortOrder: AlbumSortOrder = AlbumSortOrder.NAME,
        sortType: SortOrderType = SortOrderType.ASC
    ): Flow<List<Album>>

    /**
     * Lấy thông tin chi tiết của một Album (bao gồm Metadata)
     */
    fun getAlbum(id: Long): Flow<Album?>
}