package com.lotusreichhart.audily.feature.songs.impl.picker

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal data class SongsPickerUiState(
    val songs: Flow<PagingData<Song>> = emptyFlow(),
    val songsSelected: List<Long> = emptyList(),
    val query: String = "",
    val sortOrder: SongSortOrder = SongSortOrder.TITLE,
    val sortType: SortOrderType = SortOrderType.ASC,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false
)