package com.lotusreichhart.audily.feature.songs.impl.util

import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.feature.songs.impl.R


/**
 * Map [SongSortOrder] sang String Resource ID tương ứng.
 */
internal fun SongSortOrder.labelResId(): Int = when (this) {
    SongSortOrder.TITLE -> R.string.feature_songs_impl_sort_order_title
    SongSortOrder.ARTIST -> R.string.feature_songs_impl_sort_order_artist
    SongSortOrder.ALBUM -> R.string.feature_songs_impl_sort_order_album
    SongSortOrder.DURATION -> R.string.feature_songs_impl_sort_order_duration
    SongSortOrder.DATE_ADDED -> R.string.feature_songs_impl_sort_order_date_added
}

/**
 * Map [SortOrderType] sang String Resource ID tương ứng.
 */
internal fun SortOrderType.labelResId(): Int = when (this) {
    SortOrderType.ASC -> coreR.string.core_designsystem_sort_type_ascending
    SortOrderType.DESC -> coreR.string.core_designsystem_sort_type_descending
}
