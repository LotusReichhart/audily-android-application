package com.lotusreichhart.audily.core.data.mapper.song

import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortOrder
import com.lotusreichhart.audily.core.model.song.SongSortOrder

internal fun SongSortOrder.toMediaStoreSortOrder(): MediaStoreSortOrder = when (this) {
    SongSortOrder.TITLE_ASC -> MediaStoreSortOrder.TITLE_ASC
    SongSortOrder.TITLE_DESC -> MediaStoreSortOrder.TITLE_DESC
    SongSortOrder.ARTIST_ASC -> MediaStoreSortOrder.ARTIST_ASC
    SongSortOrder.ARTIST_DESC -> MediaStoreSortOrder.ARTIST_DESC
    SongSortOrder.ALBUM_ASC -> MediaStoreSortOrder.ALBUM_ASC
    SongSortOrder.ALBUM_DESC -> MediaStoreSortOrder.ALBUM_DESC
    SongSortOrder.DATE_ADDED_ASC -> MediaStoreSortOrder.DATE_ADDED_ASC
    SongSortOrder.DATE_ADDED_DESC -> MediaStoreSortOrder.DATE_ADDED_DESC
    SongSortOrder.DURATION_ASC -> MediaStoreSortOrder.DURATION_ASC
    SongSortOrder.DURATION_DESC -> MediaStoreSortOrder.DURATION_DESC
}
