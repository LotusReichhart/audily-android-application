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

    private val _uiEffect = MutableSharedFlow<SongsPickerUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val _songs: Flow<PagingData<Song>> =
        combine(
            _debouncedQuery,
            _sortOrder,
            _sortType
        ) { query, order, type ->
            Triple(query, order, type)
        }.flatMapLatest { (query, order, type) ->
            getSongsPagedUseCase(searchQuery = query, sortOrder = order, sortType = type)
        }.cachedIn(viewModelScope)

    val uiState: StateFlow<SongsPickerUiState> = combine(
        _query,
        _sortOrder,
        _sortType,
        combine(
            _songsSelected,
            _isSaving
        ) { selected, saving ->
            selected to saving
        }
    ) { query, sortOrder, sortType, (songsSelected, isSaving) ->
        SongsPickerUiState(
            songs = _songs,
            songsSelected = songsSelected.toList(),
            query = query,
            sortOrder = sortOrder,
            sortType = sortType,
            isLoading = false,
            isSaving = isSaving
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SongsPickerUiState(isLoading = false)
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
}