package com.lotusreichhart.audily.feature.songs.impl

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal data class SongsUiState(
    val songs: Flow<PagingData<Song>> = emptyFlow(),
    val summary: SongsSummary = SongsSummary(),
    val sortOrder: SongSortOrder = SongSortOrder.TITLE_ASC,
    val isLoading: Boolean = true
)