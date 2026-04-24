package com.lotusreichhart.audily.core.data.repository.song

import android.content.ContentResolver
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.lotusreichhart.audily.core.data.mapper.song.toSong
import com.lotusreichhart.audily.core.data.mapper.song.toSongsSummary
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.mediastore.MediaStoreIdPagingSource
import com.lotusreichhart.audily.core.mediastore.MediaStorePagingSource
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortMetadata
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

internal class SongRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val mediaStoreDataSource: MediaStoreDataSource,
) : SongRepository {

    override fun getSongsSummary(searchQuery: String?): Flow<SongsSummary> {
        return mediaStoreDataSource.getSongsSummary(searchQuery).map { it.toSongsSummary() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSongIds(
        searchQuery: String?,
        sortOrder: SongSortOrder
    ): Flow<List<Long>> {
        return mediaStoreDataSource.getSongsSortMetadata(searchQuery).map { metadataList ->
            sortMetadataList(metadataList, sortOrder).map { it.id }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSongsPaged(
        searchQuery: String?,
        sortOrder: SongSortOrder
    ): Flow<PagingData<Song>> {
        return mediaStoreDataSource.getSongsSortMetadata(searchQuery).flatMapLatest { metadataList ->
            val sortedIds = sortMetadataList(metadataList, sortOrder).map { it.id }
            
            Pager(
                config = PagingConfig(
                    pageSize = MediaStorePagingSource.PAGE_SIZE,
                    enablePlaceholders = false
                ),
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

    /**
     * Thực hiện sort danh sách Metadata trong bộ nhớ bằng Collator tiếng Việt.
     */
    private fun sortMetadataList(
        list: List<MediaStoreSortMetadata>,
        sortOrder: SongSortOrder
    ): List<MediaStoreSortMetadata> {
        val collator = Collator.getInstance(Locale("vi", "VN"))
        return when (sortOrder) {
            SongSortOrder.TITLE_ASC -> list.sortedWith { a, b -> collator.compare(a.title, b.title) }
            SongSortOrder.TITLE_DESC -> list.sortedWith { a, b -> collator.compare(b.title, a.title) }
            SongSortOrder.ARTIST_ASC -> list.sortedWith { a, b -> collator.compare(a.artist, b.artist) }
            SongSortOrder.ARTIST_DESC -> list.sortedWith { a, b -> collator.compare(b.artist, a.artist) }
            SongSortOrder.DATE_ADDED_DESC -> list.sortedByDescending { it.dateModified }
            SongSortOrder.DATE_ADDED_ASC -> list.sortedBy { it.dateModified }
            else -> list // Các tiêu chí khác chưa map hết
        }
    }
}