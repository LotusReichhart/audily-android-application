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
import com.lotusreichhart.audily.core.common.result.Result
import com.lotusreichhart.audily.core.common.result.asResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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

    private val _librarySettings = getUserPreferencesUseCase()
        .map { it.librarySettings }
        .distinctUntilChanged()

    private val _playlistsResult: StateFlow<Result<List<Playlist>>> = _librarySettings
        .flatMapLatest { settings ->
            getPlaylistsUseCase(
                sortOrder = settings.playlistSortOrder,
                sortType = settings.playlistSortType
            )
        }
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Result.Loading
        )

    private val _favoriteIds = getFavoriteIds()

    private val _favoriteSummary = getFavoriteSongsSummaryUseCase(limit = 4)

    val uiState: StateFlow<PlaylistsUiState> = combine(
        _librarySettings,
        _playlistsResult,
        _favoriteIds,
        _favoriteSummary
    ) { settings, playlistsResult, favoriteIds, favorites ->
        PlaylistsUiState(
            playlists = if (playlistsResult is Result.Success) playlistsResult.data else emptyList(),
            favoriteCount = favoriteIds.size,
            favoriteArtworkUris = favorites.map { it.basic.artworkUri },
            sortOrder = settings.playlistSortOrder,
            sortType = settings.playlistSortType,
            isLoading = playlistsResult is Result.Loading
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