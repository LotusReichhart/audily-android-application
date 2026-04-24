package com.lotusreichhart.audily.core.data.util

import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortMetadata
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import java.text.Collator
import java.util.Locale

/**
 * Utility để sắp xếp danh sách bài hát sử dụng Collator chuẩn tiếng Việt.
 * Có thể tái sử dụng cho các màn hình khác như Albums, Artists.
 */
object SongSorter {

    fun sort(
        list: List<MediaStoreSortMetadata>,
        sortOrder: SongSortOrder
    ): List<MediaStoreSortMetadata> {
        val collator = Collator.getInstance(Locale.forLanguageTag("vi-VN"))
        
        return when (sortOrder) {
            SongSortOrder.TITLE_ASC -> list.sortedWith { a, b -> collator.compare(a.title, b.title) }
            SongSortOrder.TITLE_DESC -> list.sortedWith { a, b -> collator.compare(b.title, a.title) }
            SongSortOrder.ARTIST_ASC -> list.sortedWith { a, b -> collator.compare(a.artist, b.artist) }
            SongSortOrder.ARTIST_DESC -> list.sortedWith { a, b -> collator.compare(b.artist, a.artist) }
            SongSortOrder.DATE_ADDED_DESC -> list.sortedByDescending { it.dateModified }
            SongSortOrder.DATE_ADDED_ASC -> list.sortedBy { it.dateModified }
            else -> list
        }
    }
}
