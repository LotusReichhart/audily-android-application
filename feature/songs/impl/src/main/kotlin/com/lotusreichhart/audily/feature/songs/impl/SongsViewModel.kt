package com.lotusreichhart.audily.feature.songs.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lotusreichhart.audily.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PlayNextUseCase
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
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.song.Song
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
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class SongsViewModel @Inject constructor(
    private val getSongsPagedUseCase: GetSongsPagedUseCase,
    getSongsSummaryUseCase: GetSongsSummaryUseCase,
    private val getSongIdsUseCase: GetSongIdsUseCase,
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateSongSortOrderUseCase: UpdateSongSortOrderUseCase,
    private val updateSongSortTypeUseCase: UpdateSongSortTypeUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
    private val playNextUseCase: PlayNextUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    observePlaybackStateUseCase: ObservePlaybackStateUseCase
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

    private val _songs: Flow<PagingData<Song>> = combine(_sortOrder, _sortType) { order, type ->
        order to type
    }.flatMapLatest { (order, type) ->
        getSongsPagedUseCase(sortOrder = order, sortType = type)
    }.cachedIn(viewModelScope)

    // Danh sách ID toàn bộ bài hát theo sort hiện tại (dùng cho Queue)
    private val _allSongIds: StateFlow<List<Long>> = combine(_sortOrder, _sortType) { order, type ->
        order to type
    }.flatMapLatest { (order, type) ->
        getSongIdsUseCase(sortOrder = order, sortType = type)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _isInitialLoading = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            delay(3500)
            _isInitialLoading.value = false
        }
    }

    val uiState: StateFlow<SongsUiState> = combine(
        _sortOrder,
        _sortType,
        getSongsSummaryUseCase(),
        observePlaybackStateUseCase(),
        combine(_allSongIds, _isInitialLoading) { ids, loading -> ids to loading }
    ) { sort, type, summary, playback, (allSongIds, isLoading) ->
        SongsUiState(
            songs = _songs,
            summary = summary,
            sortOrder = sort,
            sortType = type,
            playbackState = playback,
            allSongIds = allSongIds,
            isLoading = isLoading
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
