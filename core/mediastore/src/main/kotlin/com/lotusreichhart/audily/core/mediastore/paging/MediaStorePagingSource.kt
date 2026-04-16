package com.lotusreichhart.audily.core.mediastore.paging

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortOrder
import timber.log.Timber

class MediaStorePagingSource(
    private val contentResolver: ContentResolver,
    private val searchQuery: String? = null,
    private val sortOrder: MediaStoreSortOrder = MediaStoreSortOrder.TITLE_ASC,
) : PagingSource<Int, MediaStoreSong>() {

    override fun getRefreshKey(state: PagingState<Int, MediaStoreSong>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaStoreSong> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize
            val songs = queryMediaStore(offset, limit)

            LoadResult.Page(
                data = songs,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = if (songs.isEmpty()) null else offset + limit
            )
        } catch (e: Exception) {
            Timber.e(e, "Error loading MediaStore songs paging")
            LoadResult.Error(e)
        }
    }

    private fun queryMediaStore(offset: Int, limit: Int): List<MediaStoreSong> {
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

        val sqlSortOrder = sortOrder.sqlOrder
        val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val cursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val queryArgs = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection.toString())
                if (selectionArgs.isNotEmpty()) {
                    putStringArray(
                        ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                        selectionArgs.toTypedArray()
                    )
                }
                putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sqlSortOrder)
            }
            contentResolver.query(queryUri, projection, queryArgs, null)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && selectionArgs.isEmpty() && searchQuery.isNullOrBlank()) {
            val queryArgs = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sqlSortOrder)
            }
            contentResolver.query(queryUri, projection, queryArgs, null)
        } else {
            val legacySortOrder = "$sqlSortOrder LIMIT $limit OFFSET $offset"
            contentResolver.query(
                queryUri,
                projection,
                selection.toString(),
                if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
                legacySortOrder
            )
        }

        return cursor.use { mapCursorToSongs(it) }
    }

    private fun mapCursorToSongs(cursor: Cursor?): List<MediaStoreSong> {
        val songs = mutableListOf<MediaStoreSong>()
        if (cursor == null) return songs

        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

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
        return songs
    }

    companion object {
        const val PAGE_SIZE = 30
    }
}
