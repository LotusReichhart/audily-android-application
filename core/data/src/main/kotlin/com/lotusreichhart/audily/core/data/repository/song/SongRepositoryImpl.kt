package com.lotusreichhart.audily.core.data.repository.song

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.lotusreichhart.audily.core.data.mapper.song.toSong
import com.lotusreichhart.audily.core.data.mapper.song.toSongsSummary
import com.lotusreichhart.audily.core.data.util.SongSorter
import com.lotusreichhart.audily.core.data.paging.AudilyPagingConfig
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.mediastore.MediaStoreIdPagingSource
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SongRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
) : SongRepository {

    override fun getSongsSummary(searchQuery: String?): Flow<SongsSummary> {
        return mediaStoreDataSource.getSongsSummary(searchQuery).map { it.toSongsSummary() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSongIds(
        searchQuery: String?,
        sortOrder: SongSortOrder,
        sortType: SortOrderType
    ): Flow<List<Long>> {
        return mediaStoreDataSource.getSongsSortMetadata(searchQuery).map { metadataList ->
            SongSorter.sort(metadataList, sortOrder, sortType).map { it.id }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSongsPaged(
        searchQuery: String?,
        sortOrder: SongSortOrder,
        sortType: SortOrderType
    ): Flow<PagingData<Song>> {
        return mediaStoreDataSource.getSongsSortMetadata(searchQuery).flatMapLatest { metadataList ->
            val sortedIds = SongSorter.sort(metadataList, sortOrder, sortType).map { it.id }
            
            Pager(
                config = AudilyPagingConfig.defaultConfig(),
                pagingSourceFactory = {
                    MediaStoreIdPagingSource(
                        dataSources = mediaStoreDataSource,
                        sortedIds = sortedIds
                    )
                }
            ).flow.map { pagingData ->
                pagingData.map { it.toSong() }
            }
        }
    }

    override fun getSong(id: Long): Flow<Song?> {
        return flow {
            emit(mediaStoreDataSource.getSong(id)?.toSong())
        }
    }
}