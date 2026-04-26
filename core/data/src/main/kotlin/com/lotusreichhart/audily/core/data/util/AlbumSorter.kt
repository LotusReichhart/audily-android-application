package com.lotusreichhart.audily.core.data.util

import com.lotusreichhart.audily.core.mediastore.model.MediaStoreAlbumSortMetadata
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import java.text.Collator
import java.util.Locale

internal object AlbumSorter {
    private val vietnameseCollator: Collator = Collator.getInstance(Locale("vi", "VN"))

    fun sort(
        albums: List<MediaStoreAlbumSortMetadata>,
        sortOrder: AlbumSortOrder,
        sortType: SortOrderType
    ): List<MediaStoreAlbumSortMetadata> {
        val comparator = when (sortOrder) {
            AlbumSortOrder.NAME -> Comparator<MediaStoreAlbumSortMetadata> { a, b ->
                vietnameseCollator.compare(a.title, b.title)
            }
            AlbumSortOrder.ARTIST -> Comparator<MediaStoreAlbumSortMetadata> { a, b ->
                vietnameseCollator.compare(a.artist, b.artist)
            }
            AlbumSortOrder.NUMBER_OF_SONGS -> compareBy { it.songCount }
        }

        val result = if (sortType == SortOrderType.ASC) {
            albums.sortedWith(comparator)
        } else {
            albums.sortedWith(comparator.reversed())
        }

        return result
    }
}
