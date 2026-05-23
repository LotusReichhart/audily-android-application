package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    operator fun invoke(
        searchQuery: String = "",
        sortOrder: PlaylistSortOrder = PlaylistSortOrder.CREATED_DATE,
        sortType: SortOrderType = SortOrderType.ASC
    ): Flow<List<Playlist>> = playlistRepository.getPlaylists(searchQuery, sortOrder, sortType)
}