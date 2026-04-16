package com.lotusreichhart.audily.core.mediastore.model

import android.provider.MediaStore

enum class MediaStoreSortOrder(val sqlOrder: String) {
    TITLE_ASC("${MediaStore.Audio.Media.TITLE} ASC"),
    TITLE_DESC("${MediaStore.Audio.Media.TITLE} DESC"),
    ARTIST_ASC("${MediaStore.Audio.Media.ARTIST} ASC"),
    ARTIST_DESC("${MediaStore.Audio.Media.ARTIST} DESC"),
    ALBUM_ASC("${MediaStore.Audio.Media.ALBUM} ASC"),
    ALBUM_DESC("${MediaStore.Audio.Media.ALBUM} DESC"),
    DATE_ADDED_ASC("${MediaStore.Audio.Media.DATE_ADDED} ASC"),
    DATE_ADDED_DESC("${MediaStore.Audio.Media.DATE_ADDED} DESC"),
    DURATION_ASC("${MediaStore.Audio.Media.DURATION} ASC"),
    DURATION_DESC("${MediaStore.Audio.Media.DURATION} DESC")
}
