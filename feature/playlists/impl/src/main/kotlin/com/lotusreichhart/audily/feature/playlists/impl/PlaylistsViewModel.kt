package com.lotusreichhart.audily.feature.playlists.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.favorite.GetFavoriteIds
import com.lotusreichhart.audily.core.domain.usecase.favorite.GetFavoriteSongsSummaryUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.CreatePlaylistUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.GetPlaylistsUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdatePlaylistSortOrderUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdatePlaylistSortTypeUseCase
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PlaylistsViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val updatePlaylistSortOrderUseCase: UpdatePlaylistSortOrderUseCase,
    private val updatePlaylistSortTypeUseCase: UpdatePlaylistSortTypeUseCase,
    private val getFavoriteSongsSummaryUseCase: GetFavoriteSongsSummaryUseCase,
    private val getFavoriteIds: GetFavoriteIds,
    private val globalUiEventBus: GlobalUiEventBus,
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<PlaylistsUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val _isInitialLoading = MutableStateFlow(true)
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

    private val _librarySettings = getUserPreferencesUseCase()
        .map { it.librarySettings }
        .distinctUntilChanged()

    private val _playlists: Flow<List<Playlist>> = combine(
        _librarySettings,
        _isDataLoadingStarted
    ) { settings, isStarted ->
        settings to isStarted
    }.flatMapLatest { (settings, isStarted) ->
        if (!isStarted) {
            flowOf(emptyList())
        } else {
            getPlaylistsUseCase(
                sortOrder = settings.playlistSortOrder,
                sortType = settings.playlistSortType
            )
        }
    }

    private val _favoriteIds = _isDataLoadingStarted.flatMapLatest { isStarted ->
        if (!isStarted) flowOf(emptyList()) else getFavoriteIds()
    }

    private val _favoriteSummary = _isDataLoadingStarted.flatMapLatest { isStarted ->
        if (!isStarted) flowOf(emptyList()) else getFavoriteSongsSummaryUseCase(limit = 4)
    }

    val uiState: StateFlow<PlaylistsUiState> = combine(
        _librarySettings,
        _playlists,
        _favoriteIds,
        _favoriteSummary,
        _isInitialLoading
    ) { settings, playlists, favoriteIds, favorites, isLoading ->
        PlaylistsUiState(
            playlists = playlists,
            favoriteCount = favoriteIds.size,
            favoriteArtworkUris = favorites.map { it.basic.artworkUri },
            sortOrder = settings.playlistSortOrder,
            sortType = settings.playlistSortType,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlaylistsUiState(isLoading = true)
    )

    fun onEvent(event: PlaylistsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is PlaylistsUiEvent.SortOrderChanged -> updatePlaylistSortOrderUseCase(event.sortOrder)
                is PlaylistsUiEvent.SortTypeChanged -> updatePlaylistSortTypeUseCase(event.sortType)
                is PlaylistsUiEvent.CreatePlaylist -> {
                    createPlaylistUseCase(event.name, event.description)
                        .onSuccess { id ->
                            if (id > 0) {
                                globalUiEventBus.emit(
                                    GlobalUiEvent.ShowSnackbar(
                                        message = UiText.StringResource(R.string.feature_playlists_impl_playlist_created_success)
                                    )
                                )
                                _uiEffect.emit(PlaylistsUiEffect.PlaylistCreated)
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
}