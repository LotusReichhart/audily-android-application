package com.lotusreichhart.audily.core.data.util

import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortMetadata
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import java.text.Collator
import java.util.Locale

import com.lotusreichhart.audily.core.model.common.SortOrderType

/**
 * Utility để sắp xếp danh sách bài hát sử dụng Collator chuẩn tiếng Việt.
 * Có thể tái sử dụng cho các màn hình khác như Albums, Artists.
 */
object SongSorter {

    fun sort(
        list: List<MediaStoreSortMetadata>,
        sortOrder: SongSortOrder,
        sortType: SortOrderType
    ): List<MediaStoreSortMetadata> {
        val collator = Collator.getInstance(Locale.forLanguageTag("vi-VN"))
        
        return when (sortOrder) {
            SongSortOrder.TITLE -> {
                if (sortType == SortOrderType.ASC) list.sortedWith { a, b -> collator.compare(a.title, b.title) }
                else list.sortedWith { a, b -> collator.compare(b.title, a.title) }
            }
            SongSortOrder.ARTIST -> {
                if (sortType == SortOrderType.ASC) list.sortedWith { a, b -> collator.compare(a.artist, b.artist) }
                else list.sortedWith { a, b -> collator.compare(b.artist, a.artist) }
            }
            SongSortOrder.ALBUM -> {
                if (sortType == SortOrderType.ASC) list.sortedWith { a, b -> collator.compare(a.album, b.album) }
                else list.sortedWith { a, b -> collator.compare(b.album, a.album) }
            }
            SongSortOrder.DURATION -> {
                if (sortType == SortOrderType.ASC) list.sortedBy { it.duration }
                else list.sortedByDescending { it.duration }
            }
            SongSortOrder.DATE_ADDED -> {
                if (sortType == SortOrderType.ASC) list.sortedBy { it.dateModified }
                else list.sortedByDescending { it.dateModified }
            }
        }
    }
}
