package com.lotusreichhart.audily.core.data.mapper.playlist

import com.lotusreichhart.audily.core.database.model.PlaylistDaoSortOrder
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder


import com.lotusreichhart.audily.core.database.model.DaoSortOrderType
import com.lotusreichhart.audily.core.model.common.SortOrderType

internal fun PlaylistSortOrder.toPlaylistDaoSortOrder(): PlaylistDaoSortOrder = when (this) {
    PlaylistSortOrder.NAME -> PlaylistDaoSortOrder.NAME
    PlaylistSortOrder.CREATED_DATE -> PlaylistDaoSortOrder.CREATED_DATE
    PlaylistSortOrder.NUMBER_OF_SONGS -> PlaylistDaoSortOrder.NUMBER_OF_SONGS
}

internal fun SortOrderType.toDaoSortOrderType(): DaoSortOrderType = when (this) {
    SortOrderType.ASC -> DaoSortOrderType.ASC
    SortOrderType.DESC -> DaoSortOrderType.DESC
}