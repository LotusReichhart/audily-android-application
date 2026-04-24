package com.lotusreichhart.audily.feature.songs.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsPagedUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsSummaryUseCase
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class SongsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getSongsPagedUseCase: GetSongsPagedUseCase,
    private val getSongsSummaryUseCase: GetSongsSummaryUseCase,
) : ViewModel() {

    private val sortOrder = savedStateHandle.getStateFlow("sortOrder", SongSortOrder.TITLE_ASC)

    // TODO: Sprint 2.3 - Thay thế bằng logic thực tế từ core:playback
    private val _playingSongId = savedStateHandle.getStateFlow<Long?>("playingSongId", null)
    val playingSongId: StateFlow<Long?> = _playingSongId

    // TODO: Sprint 2.3 - Mock trạng thái tạm dừng
    private val _isPaused = savedStateHandle.getStateFlow("isPaused", false)
    val isPaused: StateFlow<Boolean> = _isPaused

    init {
        Timber.d("SongsViewModel Created 🟢")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("SongsViewModel Cleared 🔴")
    }

    /**
     * Luồng Paging ổn định, được giữ private vì chỉ được truy cập qua uiState.
     */
    private val _songs: Flow<PagingData<Song>> = sortOrder
        .flatMapLatest { sort ->
            Timber.d("Creating new Pager for sortOrder: $sort")
            getSongsPagedUseCase(sortOrder = sort)
        }
        .cachedIn(viewModelScope)

    val uiState: StateFlow<SongsUiState> = sortOrder
        .flatMapLatest { sort ->
            getSongsSummaryUseCase()
                .onEach { summary ->
                    Timber.d("Songs metadata summary fetched: ${summary.totalCount} songs")
                }
                .map { summary ->
                    SongsUiState.Success(
                        songs = _songs,
                        summary = summary,
                        sortOrder = sort
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SongsUiState.Loading
        )

    fun onEvent(event: SongsUiEvent) {
        when (event) {
            is SongsUiEvent.SortOrderChanged -> handleSortOrderChanged(event.sortOrder)
            is SongsUiEvent.SongClicked -> handleSongClicked(event.songId)
        }
    }

    private fun handleSortOrderChanged(newSortOrder: SongSortOrder) {
        Timber.d("Sort order changed to: $newSortOrder")
        savedStateHandle["sortOrder"] = newSortOrder
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
