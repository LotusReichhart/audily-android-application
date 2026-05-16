package com.lotusreichhart.audily.feature.playlists.impl.util

import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import com.lotusreichhart.audily.feature.playlists.impl.R

internal fun PlaylistSortOrder.labelResId(): Int = when (this) {
    PlaylistSortOrder.NAME -> R.string.feature_playlists_impl_sort_order_name
    PlaylistSortOrder.CREATED_DATE -> R.string.feature_playlists_impl_sort_order_created_date
    PlaylistSortOrder.NUMBER_OF_SONGS -> R.string.feature_playlists_impl_sort_order_number_of_songs
}

internal fun SortOrderType.labelResId(): Int = when (this) {
    SortOrderType.ASC -> coreR.string.core_designsystem_sort_type_ascending
    SortOrderType.DESC -> coreR.string.core_designsystem_sort_type_descending
}