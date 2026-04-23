package com.lotusreichhart.audily.core.data.repository.song

import android.content.ContentResolver
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.lotusreichhart.audily.core.data.mapper.song.toSong
import com.lotusreichhart.audily.core.data.mapper.song.toMediaStoreSortOrder
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.mediastore.MediaStorePagingSource
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SongRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val mediaStoreDataSource: MediaStoreDataSource,
) : SongRepository {

    override fun getSongIds(
        searchQuery: String?,
        sortOrder: SongSortOrder
    ): Flow<List<Long>> {
        return mediaStoreDataSource.getSongIds(
            searchQuery = searchQuery,
            sortOrder = sortOrder.toMediaStoreSortOrder()
        )
    }

    override fun getSongsPaged(
        searchQuery: String?,
        sortOrder: SongSortOrder
    ): Flow<PagingData<Song>> {
        return Pager(
            config = PagingConfig(
                pageSize = MediaStorePagingSource.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MediaStorePagingSource(
                    contentResolver = contentResolver,
                    searchQuery = searchQuery,
                    sortOrder = sortOrder.toMediaStoreSortOrder()
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toSong() }
        }
    }

    override fun getSong(id: Long): Flow<Song?> {
        return flow {
            emit(mediaStoreDataSource.getSong(id)?.toSong())
        }
    }
}