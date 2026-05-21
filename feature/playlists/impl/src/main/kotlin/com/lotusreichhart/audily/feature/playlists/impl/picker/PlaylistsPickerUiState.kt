package com.lotusreichhart.audily.feature.playlists.impl.picker

import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder

internal data class PlaylistsPickerUiState(
    val playlists: List<Playlist> = emptyList(),
    val playlistsSelected: List<Long> = emptyList(),
    val query: String = "",
    val sortOrder: PlaylistSortOrder = PlaylistSortOrder.NAME,
    val sortType: SortOrderType = SortOrderType.ASC,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false
)
