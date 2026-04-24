package com.lotusreichhart.audily.core.mediastore

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
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
    private val musicUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
) : PagingSource<Int, MediaStoreSong>() {

    init {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                invalidate()
            }
        }
        contentResolver.registerContentObserver(
            musicUri,
            true,
            observer
        )
        registerInvalidatedCallback {
            contentResolver.unregisterContentObserver(observer)
        }
    }

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

            val songs = contentResolver.queryBasicSongs(
                uri = musicUri,
                searchQuery = searchQuery,
                sortOrder = sortOrder,
                limit = limit,
                offset = offset
            )

            Timber.d("Loaded ${songs.size} songs at offset $offset")

            LoadResult.Page(
                data = songs,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = if (songs.size < limit) null else offset + limit
            )
        } catch (e: Exception) {
            Timber.e(e, "Error loading MediaStore songs paging")
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 30
    }
}