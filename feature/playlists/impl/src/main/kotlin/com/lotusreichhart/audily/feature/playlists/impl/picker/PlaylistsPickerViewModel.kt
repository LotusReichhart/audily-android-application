package com.lotusreichhart.audily.feature.playlists.impl.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.playlist.AddSongToPlaylistsUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.GetPlaylistsUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.CreatePlaylistUseCase
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import com.lotusreichhart.audily.feature.playlists.impl.R
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
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
internal class PlaylistsPickerViewModel @Inject constructor(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val addSongToPlaylistsUseCase: AddSongToPlaylistsUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val globalUiEventBus: GlobalUiEventBus
) : ViewModel() {
    private val _songId = MutableStateFlow<Long?>(null)
    private val _query = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    private val _debouncedQuery = _query
        .debounce(300L)
        .distinctUntilChanged()

    private val _sortOrder = MutableStateFlow(PlaylistSortOrder.NAME)
    private val _sortType = MutableStateFlow(SortOrderType.ASC)
    private val _playlistsSelected = MutableStateFlow<Set<Long>>(emptySet())
    private val _isSaving = MutableStateFlow(false)

    private val _isInitialLoading = MutableStateFlow(true)
    private val _isDataLoadingStarted = MutableStateFlow(false)

    private val _uiEffect = MutableSharedFlow<PlaylistsPickerUiEffect>()
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

    private val _playlists: Flow<List<Playlist>> =
        combine(
            _debouncedQuery,
            _sortOrder,
            _sortType,
            _isDataLoadingStarted
        ) { query, order, type, isStarted ->
            Quadruple(query, order, type, isStarted)
        }.flatMapLatest { (query, order, type, isStarted) ->
            if (!isStarted) {
                flowOf(emptyList())
            } else {
                getPlaylistsUseCase(searchQuery = query, sortOrder = order, sortType = type)
            }
        }

    val uiState: StateFlow<PlaylistsPickerUiState> = combine(
        combine(
            _query,
            _sortOrder,
            _sortType
        ) { q, o, t -> Triple(q, o, t) },
        _playlists,
        combine(
            _playlistsSelected,
            _isSaving,
            _isInitialLoading
        ) { selected, saving, loading ->
            InternalPickerState(selected, saving, loading)
        }
    ) { (query, sortOrder, sortType), playlists, state ->
        PlaylistsPickerUiState(
            playlists = playlists,
            playlistsSelected = state.playlistsSelected.toList(),
            query = query,
            sortOrder = sortOrder,
            sortType = sortType,
            isLoading = state.isLoading,
            isSaving = state.isSaving
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlaylistsPickerUiState()
    )

    fun onEvent(event: PlaylistsPickerUiEvent) {
        when (event) {
            is PlaylistsPickerUiEvent.Init -> {
                _songId.value = event.songId
            }

            is PlaylistsPickerUiEvent.OnQueryChange -> {
                _query.value = event.query
            }

            is PlaylistsPickerUiEvent.PlaylistClicked -> {
                _playlistsSelected.update { selected ->
                    if (selected.contains(event.playlistId)) {
                        selected - event.playlistId
                    } else {
                        selected + event.playlistId
                    }
                }
            }

            PlaylistsPickerUiEvent.SelectAll -> {
                val allIds = uiState.value.playlists.map { it.id }.toSet()
                val currentSelected = _playlistsSelected.value
                if (allIds.isNotEmpty() && currentSelected.containsAll(allIds)) {
                    _playlistsSelected.value = emptySet()
                } else {
                    _playlistsSelected.value = allIds
                }
            }

            is PlaylistsPickerUiEvent.SortOrderChanged -> {
                _sortOrder.value = event.sortOrder
            }
            is PlaylistsPickerUiEvent.SortTypeChanged -> {
                _sortType.value = event.sortType
            }
            PlaylistsPickerUiEvent.ClearSelection -> {
                _playlistsSelected.value = emptySet()
            }
            PlaylistsPickerUiEvent.SaveClicked -> {
                val songId = _songId.value ?: return
                val playlistIds = _playlistsSelected.value.toList()
                if (playlistIds.isEmpty()) return

                viewModelScope.launch {
                    _isSaving.value = true
                    try {
                        addSongToPlaylistsUseCase(playlistIds, songId)
                        _uiEffect.emit(PlaylistsPickerUiEffect.PlaylistsSelectedSaved)
                    } catch (e: Exception) {
                        Timber.e("SaveClicked exception: $e")
                    } finally {
                        _isSaving.value = false
                    }
                }
            }

            is PlaylistsPickerUiEvent.CreatePlaylist -> {
                viewModelScope.launch {
                    createPlaylistUseCase(event.name, event.description)
                        .onSuccess { id ->
                            if (id > 0) {
                                globalUiEventBus.emit(
                                    GlobalUiEvent.ShowSnackbar(
                                        message = UiText.StringResource(R.string.feature_playlists_impl_playlist_created_success)
                                    )
                                )
                                // Tự động chọn playlist vừa tạo
                                _playlistsSelected.update { it + id }
                                _uiEffect.emit(PlaylistsPickerUiEffect.PlaylistCreated)
                            } else {
                                globalUiEventBus.emit(
                                    GlobalUiEvent.ShowSnackbar(
                                        message = UiText.StringResource(R.string.feature_playlists_impl_playlist_created_failed)
                                    )
                                )
                            }
                        }
                        .onFailure {
                            globalUiEventBus.emit(
                                GlobalUiEvent.ShowSnackbar(
                                    message = UiText.StringResource(R.string.feature_playlists_impl_playlist_created_failed)
                                )
                            )
                        }
                }
            }
        }
    }

    private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    private data class InternalPickerState(
        val playlistsSelected: Set<Long>,
        val isSaving: Boolean,
        val isLoading: Boolean
    )
}