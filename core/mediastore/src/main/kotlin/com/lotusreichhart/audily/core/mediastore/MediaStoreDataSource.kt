package com.lotusreichhart.audily.core.mediastore

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortMetadata
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSongsSummary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaStoreDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    @param:Dispatcher(AudilyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val musicUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
) {
    /**
     * Lấy luồng thông tin tóm tắt của danh sách bài hát (số lượng, tổng thời lượng).
     */
    fun getSongsSummary(
        searchQuery: String? = null
    ): Flow<MediaStoreSongsSummary> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                launch(ioDispatcher) {
                    trySend(contentResolver.querySongsSummary(musicUri, searchQuery))
                }
            }
        }

        contentResolver.registerContentObserver(musicUri, true, observer)

        launch(ioDispatcher) {
            trySend(contentResolver.querySongsSummary(musicUri, searchQuery))
        }

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }.flowOn(ioDispatcher)

    /**
     * Lấy luồng Metadata nhẹ phục vụ cho việc sorting tiếng Việt trong bộ nhớ.
     */
    fun getSongsSortMetadata(
        searchQuery: String? = null
    ): Flow<List<MediaStoreSortMetadata>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                launch(ioDispatcher) {
                    trySend(contentResolver.querySongsSortMetadata(musicUri, searchQuery))
                }
            }
        }

        contentResolver.registerContentObserver(musicUri, true, observer)

        launch(ioDispatcher) {
            trySend(contentResolver.querySongsSortMetadata(musicUri, searchQuery))
        }

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }.flowOn(ioDispatcher)

    /**
     * Lấy đầy đủ thông tin của một bài hát (MediaStore query).
     */
    fun getSong(id: Long): MediaStoreSong? {
        return contentResolver.querySongById(musicUri, id)
    }
}