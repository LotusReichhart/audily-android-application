package com.lotusreichhart.audily.core.mediastore.model

import android.content.ContentResolver
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

enum class MediaStoreSortOrder(
    val columnName: String,
    val direction: Int,
    val isLocalized: Boolean = false
) {
    @RequiresApi(Build.VERSION_CODES.O)
    TITLE_ASC(
        columnName = MediaStore.Audio.Media.TITLE,
        direction = ContentResolver.QUERY_SORT_DIRECTION_ASCENDING,
        isLocalized = true
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    TITLE_DESC(
        columnName = MediaStore.Audio.Media.TITLE,
        direction = ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
        isLocalized = true
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    ARTIST_ASC(
        columnName = MediaStore.Audio.Media.ARTIST,
        direction = ContentResolver.QUERY_SORT_DIRECTION_ASCENDING,
        isLocalized = true
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    ARTIST_DESC(
        columnName = MediaStore.Audio.Media.ARTIST,
        direction = ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
        isLocalized = true
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    ALBUM_ASC(
        columnName = MediaStore.Audio.Media.ALBUM,
        direction = ContentResolver.QUERY_SORT_DIRECTION_ASCENDING,
        isLocalized = true
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    ALBUM_DESC(
        columnName = MediaStore.Audio.Media.ALBUM,
        direction = ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
        isLocalized = true
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    DATE_ADDED_ASC(
        columnName = MediaStore.Audio.Media.DATE_ADDED,
        direction = ContentResolver.QUERY_SORT_DIRECTION_ASCENDING,
        isLocalized = false
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    DATE_ADDED_DESC(
        columnName = MediaStore.Audio.Media.DATE_ADDED,
        direction = ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
        isLocalized = false
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    DURATION_ASC(
        columnName = MediaStore.Audio.Media.DURATION,
        direction = ContentResolver.QUERY_SORT_DIRECTION_ASCENDING,
        isLocalized = false
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    DURATION_DESC(
        columnName = MediaStore.Audio.Media.DURATION,
        direction = ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
        isLocalized = false
    );

    /**
     * Trả về chuỗi SQL Sort Order cho các API cũ (< Android 11)
     */
    val sqlOrder: String
        get() {
            val dir =
                if (direction == ContentResolver.QUERY_SORT_DIRECTION_ASCENDING) "ASC" else "DESC"
            val collation = if (isLocalized) "COLLATE LOCALIZED" else ""
            return "$columnName $collation $dir".trim()
        }
}
