package com.lotusreichhart.audily.feature.playlists.impl

import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder

internal sealed interface PlaylistsUiEvent {
    data class SortOrderChanged(val sortOrder: PlaylistSortOrder) : PlaylistsUiEvent
    data class SortTypeChanged(val sortType: SortOrderType) : PlaylistsUiEvent
    data class CreatePlaylist(val name: String, val description: String?) : PlaylistsUiEvent
}