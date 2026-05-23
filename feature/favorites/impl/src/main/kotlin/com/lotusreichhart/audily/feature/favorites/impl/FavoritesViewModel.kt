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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

@OptIn(ExperimentalCoroutinesApi::class)
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
    private val _isInitialLoading = MutableStateFlow(true)
    private val _isDataLoadingStarted = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            // Chờ animation trượt của BottomBar hoàn tất
            delay(500)
            _isDataLoadingStarted.value = true
            // Tổng thời gian hiện Shimmer
            delay(1500)
            _isInitialLoading.value = false
        }
    }

    private val _songs: Flow<PagingData<Song>> = _isDataLoadingStarted.flatMapLatest { isStarted ->
        if (!isStarted) {
            flowOf(PagingData.empty())
        } else {
            getFavoriteSongsUseCase()
        }
    }.cachedIn(viewModelScope)

    private val _favoriteSongsList = _isDataLoadingStarted.flatMapLatest { started ->
        if (started) {
            getFavoriteSongsSummaryUseCase(limit = Int.MAX_VALUE)
        } else {
            flowOf(emptyList())
        }
    }

    val uiState: StateFlow<FavoritesUiState> = combine(
        _isInitialLoading,
        _favoriteSongsList,
        observePlaybackStateUseCase()
    ) { isInitialLoading, songsList, playback ->
        val songIds = songsList.map { it.id }
        FavoritesUiState(
            songs = _songs,
            songIds = songIds,
            songsSummary = SongsSummary(
                totalCount = songsList.size,
                totalDuration = songsList.sumOf { it.basic.duration }
            ),
            artworkUris = songsList.take(4).map { it.basic.artworkUri },
            isLoading = isInitialLoading,
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