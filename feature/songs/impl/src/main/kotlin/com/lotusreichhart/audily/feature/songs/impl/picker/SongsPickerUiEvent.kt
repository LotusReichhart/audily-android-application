package com.lotusreichhart.audily.feature.songs.impl.picker

import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.SongSortOrder

internal sealed interface SongsPickerUiEvent {
    data class Init(val playlistId: Long) : SongsPickerUiEvent
    data class SortOrderChanged(val sortOrder: SongSortOrder) : SongsPickerUiEvent
    data class SortTypeChanged(val sortType: SortOrderType) : SongsPickerUiEvent
    data class SongClicked(val songId: Long) : SongsPickerUiEvent
    data class OnQueryChange(val query: String) : SongsPickerUiEvent

    object SaveClicked : SongsPickerUiEvent
    object ClearSelection : SongsPickerUiEvent
} 