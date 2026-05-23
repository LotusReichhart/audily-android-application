package com.lotusreichhart.audily.feature.albums.impl.util

import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.feature.albums.impl.R

internal fun AlbumSortOrder.labelResId(): Int = when (this) {
    AlbumSortOrder.NAME -> R.string.feature_albums_impl_sort_by_name
    AlbumSortOrder.ARTIST -> R.string.feature_albums_impl_sort_by_artist
    AlbumSortOrder.NUMBER_OF_SONGS -> R.string.feature_albums_impl_sort_by_songs_count
}

internal fun SortOrderType.labelResId(): Int = when (this) {
    SortOrderType.ASC -> coreR.string.core_designsystem_sort_type_ascending
    SortOrderType.DESC -> coreR.string.core_designsystem_sort_type_descending
}
