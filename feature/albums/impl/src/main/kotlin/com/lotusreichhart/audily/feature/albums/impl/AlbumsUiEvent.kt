package com.lotusreichhart.audily.feature.albums.impl

import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType

internal sealed interface AlbumsUiEvent {
    data class GridSizeChanged(val size: Int) : AlbumsUiEvent
    data class SortOrderChanged(val sortOrder: AlbumSortOrder) : AlbumsUiEvent
    data class SortTypeChanged(val sortType: SortOrderType) : AlbumsUiEvent
}