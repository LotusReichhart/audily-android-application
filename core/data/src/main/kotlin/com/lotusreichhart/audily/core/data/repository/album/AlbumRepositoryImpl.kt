package com.lotusreichhart.audily.core.data.repository.album

import com.lotusreichhart.audily.core.domain.repository.album.AlbumRepository
import com.lotusreichhart.audily.core.model.album.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

import com.lotusreichhart.audily.core.data.mapper.album.toAlbum
import com.lotusreichhart.audily.core.data.util.AlbumSorter
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class AlbumRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource
) : AlbumRepository {
    override fun getAlbums(
        searchQuery: String,
        sortOrder: AlbumSortOrder,
        sortType: SortOrderType
    ): Flow<List<Album>> {
        return mediaStoreDataSource.getAlbumsSortMetadata(searchQuery).map { metadataList ->
            AlbumSorter.sort(metadataList, sortOrder, sortType).map { it.toAlbum() }
        }
    }

    override fun getAlbum(id: Long): Flow<Album?> {
        return flowOf(mediaStoreDataSource.getAlbum(id)?.toAlbum())
    }
}