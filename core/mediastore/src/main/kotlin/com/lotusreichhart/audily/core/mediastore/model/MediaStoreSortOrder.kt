package com.lotusreichhart.audily.core.mediastore.model

import android.provider.MediaStore

enum class MediaStoreSortOrder(val sqlOrder: String) {
    TITLE_ASC("${MediaStore.Audio.Media.TITLE} COLLATE LOCALIZED ASC"),
    TITLE_DESC("${MediaStore.Audio.Media.TITLE} COLLATE LOCALIZED DESC"),
    ARTIST_ASC("${MediaStore.Audio.Media.ARTIST} COLLATE LOCALIZED ASC"),
    ARTIST_DESC("${MediaStore.Audio.Media.ARTIST} COLLATE LOCALIZED DESC"),
    ALBUM_ASC("${MediaStore.Audio.Media.ALBUM} COLLATE LOCALIZED ASC"),
    ALBUM_DESC("${MediaStore.Audio.Media.ALBUM} COLLATE LOCALIZED DESC"),
    DATE_ADDED_ASC("${MediaStore.Audio.Media.DATE_ADDED} ASC"),
    DATE_ADDED_DESC("${MediaStore.Audio.Media.DATE_ADDED} DESC"),
    DURATION_ASC("${MediaStore.Audio.Media.DURATION} ASC"),
    DURATION_DESC("${MediaStore.Audio.Media.DURATION} DESC")
}
