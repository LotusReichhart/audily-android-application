package com.lotusreichhart.audily.feature.songs.impl.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lotusreichhart.audily.core.domain.usecase.playlist.AddSongsToPlaylistUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsPagedUseCase
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class SongsPickerViewModel @Inject constructor(
    private val getSongsPagedUseCase: GetSongsPagedUseCase,
    private val addSongsToPlaylistUseCase: AddSongsToPlaylistUseCase
) : ViewModel() {
    private val _playlistId = MutableStateFlow<Long?>(null)
    private val _query = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    private val _debouncedQuery = _query
        .debounce(300L)
        .distinctUntilChanged()

    private val _sortOrder = MutableStateFlow(SongSortOrder.TITLE)
    private val _sortType = MutableStateFlow(SortOrderType.ASC)
    private val _songsSelected = MutableStateFlow<Set<Long>>(emptySet())
    private val _isSaving = MutableStateFlow(false)

    private val _isInitialLoading = MutableStateFlow(true)
    private val _isDataLoadingStarted = MutableStateFlow(false)

    private val _uiEffect = MutableSharedFlow<SongsPickerUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

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

    private val _songs: Flow<PagingData<Song>> =
        combine(
            _debouncedQuery,
            _sortOrder,
            _sortType,
            _isDataLoadingStarted
        ) { query, order, type, isStarted ->
            Quadruple(query, order, type, isStarted)
        }.flatMapLatest { (query, order, type, isStarted) ->
            if (!isStarted) {
                flowOf(PagingData.empty())
            } else {
                getSongsPagedUseCase(searchQuery = query, sortOrder = order, sortType = type)
            }
        }.cachedIn(viewModelScope)

    val uiState: StateFlow<SongsPickerUiState> = combine(
        _query,
        _sortOrder,
        _sortType,
        combine(
            _songsSelected,
            _isSaving,
            _isInitialLoading
        ) { selected, saving, loading ->
            InternalPickerState(selected, saving, loading)
        }
    ) { query, sortOrder, sortType, state ->
        SongsPickerUiState(
            songs = _songs,
            songsSelected = state.songsSelected.toList(),
            query = query,
            sortOrder = sortOrder,
            sortType = sortType,
            isLoading = state.isLoading,
            isSaving = state.isSaving
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SongsPickerUiState()
    )

    fun onEvent(event: SongsPickerUiEvent) {
        when (event) {
            is SongsPickerUiEvent.Init -> {
                _playlistId.value = event.playlistId
            }

            is SongsPickerUiEvent.OnQueryChange -> {
                _query.value = event.query
            }

            SongsPickerUiEvent.SaveClicked -> {
                val playlistId = _playlistId.value ?: return
                val songIds = _songsSelected.value.toList()
                if (songIds.isEmpty()) return

                viewModelScope.launch {
                    _isSaving.value = true
                    try {
                        addSongsToPlaylistUseCase(playlistId, songIds)
                        _uiEffect.emit(SongsPickerUiEffect.SongsSelectedSaved)
                    } catch (e: Exception) {
                        Timber.e("SaveClicked exception: $e")
                    } finally {
                        _isSaving.value = false
                    }
                }
            }

            is SongsPickerUiEvent.SongClicked -> {
                _songsSelected.update { selected ->
                    if (selected.contains(event.songId)) {
                        selected - event.songId
                    } else {
                        selected + event.songId
                    }
                }
            }

            is SongsPickerUiEvent.SortOrderChanged -> {
                _sortOrder.value = event.sortOrder
            }

            is SongsPickerUiEvent.SortTypeChanged -> {
                _sortType.value = event.sortType
            }

            SongsPickerUiEvent.ClearSelection -> {
                _songsSelected.value = emptySet()
            }
        }
    }

    private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    private data class InternalPickerState(
        val songsSelected: Set<Long>,
        val isSaving: Boolean,
        val isLoading: Boolean
    )
}