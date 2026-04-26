package com.lotusreichhart.audily.feature.songs.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsPagedUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsSummaryUseCase
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class SongsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getSongsPagedUseCase: GetSongsPagedUseCase,
    private val getSongsSummaryUseCase: GetSongsSummaryUseCase,
) : ViewModel() {

    private val sortOrder = savedStateHandle.getStateFlow<SongSortOrder>("sortOrder", SongSortOrder.TITLE)
    private val sortType = savedStateHandle.getStateFlow<SortOrderType>("sortType", SortOrderType.ASC)

    // TODO: Sprint 2.3 - Thay thế bằng logic thực tế từ core:playback
    private val _playingSongId = savedStateHandle.getStateFlow<Long?>("playingSongId", null)
    val playingSongId: StateFlow<Long?> = _playingSongId

    // TODO: Sprint 2.3 - Mock trạng thái tạm dừng
    private val _isPaused = savedStateHandle.getStateFlow("isPaused", false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _songs: Flow<PagingData<Song>> = combine(sortOrder, sortType, ::Pair)
        .flatMapLatest { (order, type) ->
            getSongsPagedUseCase(sortOrder = order, sortType = type)
        }
        .cachedIn(viewModelScope)
    private val _isInitialLoading = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            delay(3_000)
            _isInitialLoading.value = false
        }
    }

    val uiState: StateFlow<SongsUiState> = combine(
        sortOrder,
        sortType,
        getSongsSummaryUseCase(),
        _isInitialLoading
    ) { sort, type, summary, isLoading ->
        SongsUiState(
            songs = _songs,
            summary = summary,
            sortOrder = sort,
            sortType = type,
            isLoading = isLoading
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SongsUiState()
    )

    fun onEvent(event: SongsUiEvent) {
        when (event) {
            is SongsUiEvent.SortOrderChanged -> handleSortOrderChanged(event.sortOrder)
            is SongsUiEvent.SortTypeChanged -> handleSortTypeChanged(event.sortType)
            is SongsUiEvent.SongClicked -> handleSongClicked(event.songId)
        }
    }

    private fun handleSortOrderChanged(newSortOrder: SongSortOrder) {
        Timber.d("Sort order changed to: $newSortOrder")
        savedStateHandle["sortOrder"] = newSortOrder
    }

    private fun handleSortTypeChanged(newSortType: SortOrderType) {
        Timber.d("Sort type changed to: $newSortType")
        savedStateHandle["sortType"] = newSortType
    }

    // TODO: Sprint 2.3 - Xóa bỏ khi tích hợp Playback thật
    private fun handleSongClicked(songId: Long) {
        val current = _playingSongId.value
        if (current == songId) {
            savedStateHandle["isPaused"] = !_isPaused.value
        } else {
            savedStateHandle["playingSongId"] = songId
            savedStateHandle["isPaused"] = false
        }
        Timber.d(
            "Faking song click for visual testing: $songId (Paused: ${
                savedStateHandle.get<Boolean>(
                    "isPaused"
                )
            })"
        )
    }
}
