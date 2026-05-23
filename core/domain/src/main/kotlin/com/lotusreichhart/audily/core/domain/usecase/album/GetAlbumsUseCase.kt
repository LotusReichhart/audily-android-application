package com.lotusreichhart.audily.core.domain.usecase.album

import com.lotusreichhart.audily.core.domain.repository.album.AlbumRepository
import com.lotusreichhart.audily.core.model.album.Album
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(
        searchQuery: String = "",
        sortOrder: AlbumSortOrder = AlbumSortOrder.NAME,
        sortType: SortOrderType = SortOrderType.ASC
    ): Flow<List<Album>> = albumRepository.getAlbums(searchQuery, sortOrder, sortType)
}