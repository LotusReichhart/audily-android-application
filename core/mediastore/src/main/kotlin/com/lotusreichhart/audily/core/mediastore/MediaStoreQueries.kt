package com.lotusreichhart.audily.core.mediastore

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.lotusreichhart.audily.core.mediastore.model.BasicMediaStoreMetadata
import com.lotusreichhart.audily.core.mediastore.model.ExtendedMediaStoreMetadata
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortOrder

/**
 * Các hàm helper nội bộ để truy vấn MediaStore
 */

internal fun ContentResolver.queryBasicSongs(
    uri: Uri,
    searchQuery: String? = null,
    sortOrder: MediaStoreSortOrder = MediaStoreSortOrder.TITLE_ASC,
    limit: Int? = null,
    offset: Int? = null
): List<MediaStoreSong> {
    val songs = mutableListOf<MediaStoreSong>()
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATE_MODIFIED
    )

    val selection = StringBuilder("${MediaStore.Audio.Media.IS_MUSIC} != 0")
    val selectionArgs = mutableListOf<String>()

    if (!searchQuery.isNullOrBlank()) {
        selection.append(" AND (${MediaStore.Audio.Media.TITLE} LIKE ? OR ${MediaStore.Audio.Media.ARTIST} LIKE ?)")
        selectionArgs.add("%$searchQuery%")
        selectionArgs.add("%$searchQuery%")
    }

    val queryUri = uri
    
    val cursor = if (limit != null && offset != null) {
        // Phân trang
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val queryArgs = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection.toString())
                if (selectionArgs.isNotEmpty()) {
                    putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs.toTypedArray())
                }
                putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortOrder.sqlOrder)
            }
            this.query(queryUri, projection, queryArgs, null)
        } else {
            val legacySortOrder = "${sortOrder.sqlOrder} LIMIT $limit OFFSET $offset"
            this.query(
                queryUri,
                projection,
                selection.toString(),
                if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
                legacySortOrder
            )
        }
    } else {
        // Không phân trang
        this.query(
            queryUri,
            projection,
            selection.toString(),
            if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
            sortOrder.sqlOrder
        )
    }

    cursor?.use { c ->
        val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dataCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val albumIdCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val dateModCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

        while (c.moveToNext()) {
            songs.add(
                MediaStoreSong(
                    id = c.getLong(idCol),
                    basic = BasicMediaStoreMetadata(
                        title = c.getString(titleCol),
                        artist = c.getString(artistCol),
                        album = c.getString(albumCol),
                        duration = c.getLong(durationCol),
                        path = c.getString(dataCol),
                        albumId = c.getLong(albumIdCol),
                        dateModified = c.getLong(dateModCol)
                    ),
                    extended = null
                )
            )
        }
    }
    return songs
}

internal fun ContentResolver.querySongIds(
    uri: Uri,
    searchQuery: String? = null,
    sortOrder: MediaStoreSortOrder = MediaStoreSortOrder.TITLE_ASC
): List<Long> {
    val ids = mutableListOf<Long>()
    val projection = arrayOf(MediaStore.Audio.Media._ID)

    val selection = StringBuilder("${MediaStore.Audio.Media.IS_MUSIC} != 0")
    val selectionArgs = mutableListOf<String>()

    if (!searchQuery.isNullOrBlank()) {
        selection.append(" AND (${MediaStore.Audio.Media.TITLE} LIKE ? OR ${MediaStore.Audio.Media.ARTIST} LIKE ?)")
        selectionArgs.add("%$searchQuery%")
        selectionArgs.add("%$searchQuery%")
    }

    this.query(
        uri,
        projection,
        selection.toString(),
        if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
        sortOrder.sqlOrder
    )?.use { c ->
        val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        while (c.moveToNext()) {
            ids.add(c.getLong(idCol))
        }
    }
    return ids
}

internal fun ContentResolver.querySongById(
    uri: Uri,
    id: Long
): MediaStoreSong? {
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.COMPOSER
    )

    return this.query(
        uri,
        projection,
        "${MediaStore.Audio.Media._ID} = ?",
        arrayOf(id.toString()),
        null
    )?.use { c ->
        if (c.moveToFirst()) {
            val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dateModCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val trackCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val sizeCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val composerCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)

            MediaStoreSong(
                id = c.getLong(idCol),
                basic = BasicMediaStoreMetadata(
                    title = c.getString(titleCol),
                    artist = c.getString(artistCol),
                    album = c.getString(albumCol),
                    duration = c.getLong(durationCol),
                    path = c.getString(dataCol),
                    albumId = c.getLong(albumIdCol),
                    dateModified = c.getLong(dateModCol)
                ),
                extended = ExtendedMediaStoreMetadata(
                    track = c.getInt(trackCol),
                    year = c.getInt(yearCol),
                    size = c.getLong(sizeCol),
                    composer = c.getString(composerCol)
                )
            )
        } else null
    }
}
