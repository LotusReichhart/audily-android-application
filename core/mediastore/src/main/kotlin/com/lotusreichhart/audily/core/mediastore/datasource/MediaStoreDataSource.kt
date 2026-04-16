package com.lotusreichhart.audily.core.mediastore.datasource

import android.content.ContentResolver
import android.database.ContentObserver
import android.provider.MediaStore
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaStoreDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    @param:Dispatcher(AudilyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    fun getSongs(
        searchQuery: String? = null,
        sortOrder: MediaStoreSortOrder = MediaStoreSortOrder.TITLE_ASC
    ): Flow<List<MediaStoreSong>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                launch(ioDispatcher) {
                    trySend(querySongs(searchQuery, sortOrder))
                }
            }
        }

        contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )

        // Phát dữ liệu lần đầu tiên trên IO thread
        launch(ioDispatcher) {
            trySend(querySongs(searchQuery, sortOrder))
        }

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }.flowOn(ioDispatcher)

    private fun querySongs(
        searchQuery: String? = null,
        sortOrder: MediaStoreSortOrder = MediaStoreSortOrder.TITLE_ASC
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

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection.toString(),
            if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
            sortOrder.sqlOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                songs.add(
                    MediaStoreSong(
                        id = cursor.getLong(idColumn),
                        title = cursor.getString(titleColumn),
                        artist = cursor.getString(artistColumn),
                        album = cursor.getString(albumColumn),
                        duration = cursor.getLong(durationColumn),
                        data = cursor.getString(dataColumn),
                        albumId = cursor.getLong(albumIdColumn),
                        dateModified = cursor.getLong(dateModifiedColumn)
                    )
                )
            }
        }
        return songs
    }

    fun getSong(id: Long): MediaStoreSong? {
        return querySongs().find { it.id == id }
    }
}
