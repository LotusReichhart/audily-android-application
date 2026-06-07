package com.lotusreichhart.audily.feature.songs.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lotusreichhart.audily.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayNextUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PauseSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateSongSortOrderUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateSongSortTypeUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongIdsUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsPagedUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsSummaryUseCase
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import com.lotusreichhart.audily.core.common.result.Result
import com.lotusreichhart.audily.core.common.result.asResult
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class SongsViewModel @Inject constructor(
    observePlaybackStateUseCase: ObservePlaybackStateUseCase,
    getSongsSummaryUseCase: GetSongsSummaryUseCase,
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val getSongsPagedUseCase: GetSongsPagedUseCase,
    private val getSongIdsUseCase: GetSongIdsUseCase,
    private val updateSongSortOrderUseCase: UpdateSongSortOrderUseCase,
    private val updateSongSortTypeUseCase: UpdateSongSortTypeUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
    private val playNextUseCase: PlayNextUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val pauseSongUseCase: PauseSongUseCase
) : ViewModel() {

    private val _userPrefs = getUserPreferencesUseCase()
        .map { it.librarySettings }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _sortOrder = _userPrefs.map { it?.songSortOrder ?: SongsUiState().sortOrder }
    private val _sortType = _userPrefs.map { it?.songSortType ?: SongsUiState().sortType }

    private val _isRefreshing = MutableStateFlow(false)
    private val _wasRefreshed = MutableStateFlow(false)

    private val _summaryResult = getSongsSummaryUseCase()
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Result.Loading
        )

    private val _songs: Flow<PagingData<Song>> =
        combine(
            _sortOrder,
            _sortType
        ) { order, type ->
            order to type
        }.flatMapLatest { (order, type) ->
            getSongsPagedUseCase(sortOrder = order, sortType = type)
        }.cachedIn(viewModelScope)

    // Danh sách ID toàn bộ bài hát theo sort hiện tại (dùng cho Queue)
    private val _allSongIds: StateFlow<List<Long>> =
        combine(
            _sortOrder,
            _sortType
        ) { order, type ->
            order to type
        }.flatMapLatest { (order, type) ->
            getSongIdsUseCase(sortOrder = order, sortType = type)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<SongsUiState> = combine(
        _sortOrder,
        _sortType,
        _summaryResult,
        observePlaybackStateUseCase(),
        combine(
            _allSongIds,
            _isRefreshing,
            _wasRefreshed
        ) { ids, refreshing, refreshed ->
            Triple(ids, refreshing, refreshed)
        }
    ) { sort, type, summaryResult, playback, state ->
        val summary = if (summaryResult is Result.Success) summaryResult.data else SongsSummary()
        SongsUiState(
            songs = _songs,
            summary = summary,
            sortOrder = sort,
            sortType = type,
            playbackState = playback,
            allSongIds = state.first,
            isLoading = summaryResult is Result.Loading,
            isRefreshing = state.second,
            wasRefreshed = state.third
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SongsUiState()
    )

    fun onEvent(event: SongsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is SongsUiEvent.SortOrderChanged -> updateSongSortOrderUseCase(event.sortOrder)
                is SongsUiEvent.SortTypeChanged -> updateSongSortTypeUseCase(event.sortType)
                is SongsUiEvent.SongClicked -> handleSongClicked(event.songId)
                is SongsUiEvent.PlayNextClicked -> handlePlayNext(event.song)
                is SongsUiEvent.ToggleFavoriteClicked -> toggleFavoriteUseCase(event.songId)
                is SongsUiEvent.Refresh -> {
                    _isRefreshing.value = true
                    updateSongSortOrderUseCase(SongSortOrder.TITLE)
                    updateSongSortTypeUseCase(SortOrderType.ASC)
                    _isRefreshing.value = false
                    _wasRefreshed.value = true
                }
            }
        }
    }

    private suspend fun handleSongClicked(songId: Long) {
        val state = uiState.value
        val currentPlayback = state.playbackState
        Timber.d("Current Playback: $currentPlayback")
        if (currentPlayback.currentSongId == songId) {
            if (currentPlayback.nowPlayingState == NowPlayingState.PLAYING) {
                pauseSongUseCase()
            } else {
                resumeSongUseCase()
            }
        } else {
            Timber.d("Start New Playing")
            playFromQueueUseCase(songId, state.allSongIds)
        }
    }

    private suspend fun handlePlayNext(song: Song) {
        // Nếu là bài đang phát -> không làm gì
        if (uiState.value.playbackState.currentSongId == song.id) return

        playNextUseCase(song)
    }
}
