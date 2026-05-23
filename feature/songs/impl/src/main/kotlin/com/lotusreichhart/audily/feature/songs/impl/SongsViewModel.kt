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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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

    private val _isInitialLoading = MutableStateFlow(true)
    private val _isRefreshing = MutableStateFlow(false)
    private val _wasRefreshed = MutableStateFlow(false)
    private val _isDataLoadingStarted = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            // Chờ animation trượt của BottomBar hoàn tất
            delay(1000)
            _isDataLoadingStarted.value = true
            // Tổng thời gian hiện Shimmer
            delay(2000)
            _isInitialLoading.value = false
        }
    }

    private val _songs: Flow<PagingData<Song>> =
        combine(
            _sortOrder,
            _sortType,
            _isDataLoadingStarted,
            _isRefreshing
        ) { order, type, isStarted, isRefreshing ->
            Quadruple(order, type, isStarted, isRefreshing)
        }.flatMapLatest { (order, type, isStarted, isRefreshing) ->
            if (!isStarted || isRefreshing) {
                flowOf(PagingData.empty())
            } else {
                getSongsPagedUseCase(sortOrder = order, sortType = type)
            }
        }.cachedIn(viewModelScope)

    // Danh sách ID toàn bộ bài hát theo sort hiện tại (dùng cho Queue)
    private val _allSongIds: StateFlow<List<Long>> =
        combine(
            _sortOrder,
            _sortType,
            _isDataLoadingStarted,
            _isRefreshing
        ) { order, type, isStarted, isRefreshing ->
            Quadruple(order, type, isStarted, isRefreshing)
        }.flatMapLatest { (order, type, isStarted, isRefreshing) ->
            if (!isStarted || isRefreshing) {
                flowOf(emptyList())
            } else {
                getSongIdsUseCase(sortOrder = order, sortType = type)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<SongsUiState> = combine(
        _sortOrder,
        _sortType,
        getSongsSummaryUseCase(),
        observePlaybackStateUseCase(),
        combine(
            _allSongIds,
            _isInitialLoading,
            _isRefreshing,
            _wasRefreshed
        ) { ids, loading, refreshing, refreshed ->
            RefreshedState(ids, loading, refreshing, refreshed)
        }
    ) { sort, type, summary, playback, state ->
        SongsUiState(
            songs = _songs,
            summary = summary,
            sortOrder = sort,
            sortType = type,
            playbackState = playback,
            allSongIds = state.allSongIds,
            isLoading = state.isLoading,
            isRefreshing = state.isRefreshing,
            wasRefreshed = state.wasRefreshed
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
                    delay(2000)
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

    private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    private data class RefreshedState(
        val allSongIds: List<Long>,
        val isLoading: Boolean,
        val isRefreshing: Boolean,
        val wasRefreshed: Boolean
    )
}
