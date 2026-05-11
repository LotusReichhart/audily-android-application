package com.lotusreichhart.audily.core.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.lotusreichhart.audily.core.mediastore.model.BasicMediaStoreMetadata
import com.lotusreichhart.audily.core.mediastore.model.ExtendedMediaStoreMetadata
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreAlbum
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreAlbumSortMetadata
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSongsSummary
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortMetadata
import androidx.core.net.toUri

/**
 * Các hàm helper nội bộ để truy vấn MediaStore
 */

/**
 * Tạo URI cho ảnh album từ albumId.
 */
private fun getArtworkUri(albumId: Long): String {
    return ContentUris.withAppendedId(
        "content://media/external/audio/albumart".toUri(),
        albumId
    ).toString()
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

            val albumId = c.getLong(albumIdCol)
            MediaStoreSong(
                id = c.getLong(idCol),
                basic = BasicMediaStoreMetadata(
                    title = c.getString(titleCol) ?: "Unknown Title",
                    artist = c.getString(artistCol) ?: "Unknown Artist",
                    album = c.getString(albumCol) ?: "Unknown Album",
                    duration = c.getLong(durationCol),
                    path = c.getString(dataCol) ?: "",
                    albumId = albumId,
                    dateModified = c.getLong(dateModCol),
                    artworkUri = getArtworkUri(albumId)
                ),
                extended = ExtendedMediaStoreMetadata(
                    track = c.getInt(trackCol),
                    year = c.getInt(yearCol),
                    size = c.getLong(sizeCol),
                    composer = c.getString(composerCol) ?: "Unknown Composer"
                )
            )
        } else null
    }
}

internal fun ContentResolver.querySongsByIds(
    uri: Uri,
    ids: List<Long>
): List<MediaStoreSong> {
    if (ids.isEmpty()) return emptyList()

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

    // Chia nhỏ danh sách nếu quá lớn để tránh giới hạn tham số SQL
    val chunks = ids.chunked(500)
    val results = mutableListOf<MediaStoreSong>()

    chunks.forEach { chunk ->
        val selection = "${MediaStore.Audio.Media._ID} IN (${chunk.joinToString(",")})"
        this.query(uri, projection, selection, null, null)?.use { c ->
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

            while (c.moveToNext()) {
                val albumId = c.getLong(albumIdCol)
                results.add(
                    MediaStoreSong(
                        id = c.getLong(idCol),
                        basic = BasicMediaStoreMetadata(
                            title = c.getString(titleCol) ?: "Unknown Title",
                            artist = c.getString(artistCol) ?: "Unknown Artist",
                            album = c.getString(albumCol) ?: "Unknown Album",
                            duration = c.getLong(durationCol),
                            path = c.getString(dataCol) ?: "",
                            albumId = albumId,
                            dateModified = c.getLong(dateModCol),
                            artworkUri = getArtworkUri(albumId)
                        ),
                        extended = ExtendedMediaStoreMetadata(
                            track = c.getInt(trackCol),
                            year = c.getInt(yearCol),
                            size = c.getLong(sizeCol),
                            composer = c.getString(composerCol) ?: "Unknown Composer"
                        )
                    )
                )
            }
        }
    }

    // Duy trì thứ tự của danh sách IDs truyền vào
    val resultMap = results.associateBy { it.id }
    return ids.mapNotNull { resultMap[it] }
}

internal fun ContentResolver.queryBasicSongById(
    uri: Uri,
    id: Long
): MediaStoreSong? {
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID
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
            val albumIdCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            val albumId = c.getLong(albumIdCol)
            MediaStoreSong(
                id = c.getLong(idCol),
                basic = BasicMediaStoreMetadata(
                    title = c.getString(titleCol) ?: "Unknown Title",
                    artist = c.getString(artistCol) ?: "Unknown Artist",
                    album = c.getString(albumCol) ?: "Unknown Album",
                    duration = c.getLong(durationCol),
                    path = "", // Không lấy path cho Basic query
                    albumId = albumId,
                    dateModified = 0L,
                    artworkUri = getArtworkUri(albumId)
                ),
                extended = ExtendedMediaStoreMetadata(
                    track = 0,
                    year = 0,
                    size = 0L,
                    composer = ""
                )
            )
        } else null
    }
}

internal fun ContentResolver.queryBasicSongsByIds(
    uri: Uri,
    ids: List<Long>
): List<MediaStoreSong> {
    if (ids.isEmpty()) return emptyList()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID
    )

    val chunks = ids.chunked(500)
    val results = mutableListOf<MediaStoreSong>()

    chunks.forEach { chunk ->
        val selection = "${MediaStore.Audio.Media._ID} IN (${chunk.joinToString(",")})"
        this.query(uri, projection, selection, null, null)?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (c.moveToNext()) {
                val albumId = c.getLong(albumIdCol)
                results.add(
                    MediaStoreSong(
                        id = c.getLong(idCol),
                        basic = BasicMediaStoreMetadata(
                            title = c.getString(titleCol) ?: "Unknown Title",
                            artist = c.getString(artistCol) ?: "Unknown Artist",
                            album = c.getString(albumCol) ?: "Unknown Album",
                            duration = c.getLong(durationCol),
                            path = "", // Không lấy path cho Basic query
                            albumId = albumId,
                            dateModified = 0L,
                            artworkUri = getArtworkUri(albumId)
                        ),
                        extended = ExtendedMediaStoreMetadata(
                            track = 0,
                            year = 0,
                            size = 0L,
                            composer = ""
                        )
                    )
                )
            }
        }
    }

    val resultMap = results.associateBy { it.id }
    return ids.mapNotNull { resultMap[it] }
}

internal fun ContentResolver.querySongsSortMetadata(
    uri: Uri,
    searchQuery: String? = null
): List<MediaStoreSortMetadata> {
    val metadataList = mutableListOf<MediaStoreSortMetadata>()
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATE_MODIFIED
    )

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
        "${MediaStore.Audio.Media.TITLE} ASC"
    )?.use { c ->
        val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dateModCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

        while (c.moveToNext()) {
            metadataList.add(
                MediaStoreSortMetadata(
                    id = c.getLong(idCol),
                    title = c.getString(titleCol) ?: "Unknown Title",
                    artist = c.getString(artistCol) ?: "Unknown Artist",
                    album = c.getString(albumCol) ?: "Unknown Album",
                    duration = c.getLong(durationCol),
                    dateModified = c.getLong(dateModCol)
                )
            )
        }
    }
    return metadataList
}

internal fun ContentResolver.querySongsSummary(
    uri: Uri,
    searchQuery: String? = null
): MediaStoreSongsSummary {
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DURATION
    )

    val selection = StringBuilder("${MediaStore.Audio.Media.IS_MUSIC} != 0")
    val selectionArgs = mutableListOf<String>()

    if (!searchQuery.isNullOrBlank()) {
        selection.append(" AND (${MediaStore.Audio.Media.TITLE} LIKE ? OR ${MediaStore.Audio.Media.ARTIST} LIKE ?)")
        selectionArgs.add("%$searchQuery%")
        selectionArgs.add("%$searchQuery%")
    }

    var count = 0
    var totalDuration = 0L

    this.query(
        uri,
        projection,
        selection.toString(),
        if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
        null
    )?.use { c ->
        count = c.count
        val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        while (c.moveToNext()) {
            totalDuration += c.getLong(durationCol)
        }
    }

    return MediaStoreSongsSummary(count, totalDuration)
}

internal fun ContentResolver.queryAlbums(
    uri: Uri,
    searchQuery: String? = null
): List<MediaStoreAlbumSortMetadata> {
    val albums = mutableListOf<MediaStoreAlbumSortMetadata>()
    val projection = arrayOf(
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS
    )

    val selection = if (!searchQuery.isNullOrBlank()) {
        "${MediaStore.Audio.Albums.ALBUM} LIKE ? OR ${MediaStore.Audio.Albums.ARTIST} LIKE ?"
    } else null
    val selectionArgs = if (!searchQuery.isNullOrBlank()) {
        arrayOf("%$searchQuery%", "%$searchQuery%")
    } else null

    this.query(
        uri,
        projection,
        selection,
        selectionArgs,
        "${MediaStore.Audio.Albums.ALBUM} ASC"
    )?.use { c ->
        val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
        val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
        val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
        val countCol = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

        while (c.moveToNext()) {
            val albumId = c.getLong(idCol)
            albums.add(
                MediaStoreAlbumSortMetadata(
                    id = c.getLong(idCol),
                    title = c.getString(albumCol) ?: "Unknown Album",
                    artist = c.getString(artistCol) ?: "Unknown Artist",
                    albumArtUri = getArtworkUri(albumId),
                    songCount = c.getInt(countCol)
                )
            )
        }
    }
    return albums
}

internal fun ContentResolver.queryAlbumById(
    uri: Uri,
    id: Long
): MediaStoreAlbum? {
    val projection = arrayOf(
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS
    )

    return this.query(
        uri,
        projection,
        "${MediaStore.Audio.Albums._ID} = ?",
        arrayOf(id.toString()),
        null
    )?.use { c ->
        if (c.moveToFirst()) {
            val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val countCol = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            val albumId = c.getLong(idCol)
            MediaStoreAlbum(
                id = albumId,
                title = c.getString(albumCol) ?: "Unknown Album",
                artist = c.getString(artistCol) ?: "Unknown Artist",
                albumArtUri = getArtworkUri(albumId),
                songCount = c.getInt(countCol)
            )
        } else null
    }
}
