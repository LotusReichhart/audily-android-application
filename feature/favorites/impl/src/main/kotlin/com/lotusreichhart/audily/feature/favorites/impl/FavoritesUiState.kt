package com.lotusreichhart.audily.feature.favorites.impl

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal data class FavoritesUiState(
    val songs: Flow<PagingData<Song>> = emptyFlow(),
    val songIds: List<Long> = emptyList(),
    val songsSummary: SongsSummary = SongsSummary(),
    val artworkUris: List<String?> = emptyList(),
    val isLoading: Boolean = true,
    val playbackState: PlaybackState = PlaybackState.INITIAL
)
