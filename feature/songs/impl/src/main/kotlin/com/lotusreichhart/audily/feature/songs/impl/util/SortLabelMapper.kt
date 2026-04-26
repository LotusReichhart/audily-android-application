package com.lotusreichhart.audily.feature.songs.impl.util

import com.lotusreichhart.audily.core.designsystem.R
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.SongSortOrder

/**
 * Map [SongSortOrder] sang String Resource ID tương ứng.
 */
internal fun SongSortOrder.labelResId(): Int = when (this) {
    SongSortOrder.TITLE -> R.string.core_designsystem_sort_order_title
    SongSortOrder.ARTIST -> R.string.core_designsystem_sort_order_artist
    SongSortOrder.ALBUM -> R.string.core_designsystem_sort_order_album
    SongSortOrder.DURATION -> R.string.core_designsystem_sort_order_duration
    SongSortOrder.DATE_ADDED -> R.string.core_designsystem_sort_order_date_added
}

/**
 * Map [SortOrderType] sang String Resource ID tương ứng.
 */
internal fun SortOrderType.labelResId(): Int = when (this) {
    SortOrderType.ASC -> R.string.core_designsystem_sort_type_ascending
    SortOrderType.DESC -> R.string.core_designsystem_sort_type_descending
}
