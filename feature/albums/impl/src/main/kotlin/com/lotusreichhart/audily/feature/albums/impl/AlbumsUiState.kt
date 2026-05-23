package com.lotusreichhart.audily.feature.albums.impl

import com.lotusreichhart.audily.core.model.album.Album
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType

internal data class AlbumsUiState(
    val albums: List<Album> = emptyList(),
    val gridSize: Int = 1,
    val sortOrder: AlbumSortOrder = AlbumSortOrder.NAME,
    val sortType: SortOrderType = SortOrderType.ASC,
    val isLoading: Boolean = false
)
