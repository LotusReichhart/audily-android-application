package com.lotusreichhart.audily.core.data.mapper.playlist

import com.lotusreichhart.audily.core.database.model.PlaylistDaoSortOrder
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder


internal fun PlaylistSortOrder.toPlaylistDaoSortOrder(): PlaylistDaoSortOrder = when (this) {
    PlaylistSortOrder.NAME_ASC -> PlaylistDaoSortOrder.NAME_ASC
    PlaylistSortOrder.NAME_DESC -> PlaylistDaoSortOrder.NAME_DESC
    PlaylistSortOrder.CREATED_DATE_ASC -> PlaylistDaoSortOrder.CREATED_DATE_ASC
    PlaylistSortOrder.CREATED_DATE_DESC -> PlaylistDaoSortOrder.CREATED_DATE_DESC
}