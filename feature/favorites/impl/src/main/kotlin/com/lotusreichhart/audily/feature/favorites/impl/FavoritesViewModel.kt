package com.lotusreichhart.audily.feature.favorites.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lotusreichhart.audily.core.domain.usecase.favorite.ClearFavoritesUseCase
import com.lotusreichhart.audily.core.domain.usecase.favorite.GetFavoriteSongsSummaryUseCase
import com.lotusreichhart.audily.core.domain.usecase.favorite.GetFavoriteSongsUseCase
import com.lotusreichhart.audily.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PauseSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongsSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import com.lotusreichhart.audily.core.common.result.Result
import com.lotusreichhart.audily.core.common.result.asResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
internal class FavoritesViewModel @Inject constructor(
    observePlaybackStateUseCase: ObservePlaybackStateUseCase,
    private val getFavoriteSongsUseCase: GetFavoriteSongsUseCase,
    private val getFavoriteSongsSummaryUseCase: GetFavoriteSongsSummaryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val clearFavoritesUseCase: ClearFavoritesUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val pauseSongUseCase: PauseSongUseCase
) : ViewModel() {

    private val _songs: Flow<PagingData<Song>> = getFavoriteSongsUseCase()
        .cachedIn(viewModelScope)

    private val _favoriteSongsResult = getFavoriteSongsSummaryUseCase(limit = Int.MAX_VALUE)
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Result.Loading
        )

    val uiState: StateFlow<FavoritesUiState> = combine(
        _favoriteSongsResult,
        observePlaybackStateUseCase()
    ) { favoriteSongsResult, playback ->
        val songsList = if (favoriteSongsResult is Result.Success) favoriteSongsResult.data else emptyList()
        val songIds = songsList.map { it.id }
        FavoritesUiState(
            songs = _songs,
            songIds = songIds,
            songsSummary = SongsSummary(
                totalCount = songsList.size,
                totalDuration = songsList.sumOf { it.basic.duration }
            ),
            artworkUris = songsList.take(4).map { it.basic.artworkUri },
            isLoading = favoriteSongsResult is Result.Loading,
            playbackState = playback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FavoritesUiState(isLoading = true)
    )

    fun onEvent(event: FavoritesUiEvent) {
        viewModelScope.launch {
            when (event) {
                is FavoritesUiEvent.SongClicked -> {
                    if (event.isMissing) return@launch
                    val currentPlayback = uiState.value.playbackState
                    if (currentPlayback.currentSongId == event.songId) {
                        if (currentPlayback.nowPlayingState == NowPlayingState.PLAYING) {
                            pauseSongUseCase()
                        } else {
                            resumeSongUseCase()
                        }
                    } else {
                        playFromQueueUseCase(
                            songId = event.songId,
                            queueIds = uiState.value.songIds
                        )
                    }
                }

                is FavoritesUiEvent.RemoveSong -> {
                    toggleFavoriteUseCase(event.songId)
                }

                FavoritesUiEvent.PlayAll -> {
                    val songIds = uiState.value.songIds
                    if (songIds.isNotEmpty()) {
                        playFromQueueUseCase(songId = songIds.first(), queueIds = songIds)
                    }
                }

                FavoritesUiEvent.DeleteAll -> {
                    clearFavoritesUseCase()
                }
            }
        }
    }
}