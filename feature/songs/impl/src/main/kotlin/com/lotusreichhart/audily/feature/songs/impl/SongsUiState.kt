package com.lotusreichhart.audily.feature.songs.impl

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal data class SongsUiState(
    val songs: Flow<PagingData<Song>> = emptyFlow(),
    val summary: SongsSummary = SongsSummary(),
    val sortOrder: SongSortOrder = SongSortOrder.TITLE,
    val sortType: SortOrderType = SortOrderType.ASC,
    val playbackState: PlaybackState = PlaybackState.INITIAL,
    val allSongIds: List<Long> = emptyList(),
    val isLoading: Boolean = true
)