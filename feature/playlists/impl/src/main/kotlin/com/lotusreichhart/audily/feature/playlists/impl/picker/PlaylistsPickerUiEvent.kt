package com.lotusreichhart.audily.feature.playlists.impl.picker

import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder

internal sealed interface PlaylistsPickerUiEvent {
    data class Init(val songId: Long) : PlaylistsPickerUiEvent

    data class SortOrderChanged(val sortOrder: PlaylistSortOrder) : PlaylistsPickerUiEvent
    data class SortTypeChanged(val sortType: SortOrderType) : PlaylistsPickerUiEvent
    data class PlaylistClicked(val playlistId: Long) : PlaylistsPickerUiEvent
    data class OnQueryChange(val query: String) : PlaylistsPickerUiEvent

    object SelectAll : PlaylistsPickerUiEvent
    object SaveClicked : PlaylistsPickerUiEvent
    object ClearSelection : PlaylistsPickerUiEvent
    data class CreatePlaylist(val name: String, val description: String?) : PlaylistsPickerUiEvent
}