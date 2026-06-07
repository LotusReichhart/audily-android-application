package com.lotusreichhart.audily.feature.albums.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.album.GetAlbumsUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAlbumGridSizeUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAlbumSortOrderUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAlbumSortTypeUseCase
import com.lotusreichhart.audily.core.model.album.Album
import com.lotusreichhart.audily.core.common.result.Result
import com.lotusreichhart.audily.core.common.result.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AlbumsViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val updateAlbumsGridSizeUseCase: UpdateAlbumGridSizeUseCase,
    private val updateAlbumSortOrderUseCase: UpdateAlbumSortOrderUseCase,
    private val updateAlbumSortTypeUseCase: UpdateAlbumSortTypeUseCase
) : ViewModel() {

    private val _librarySettings = getUserPreferencesUseCase()
        .map { it.librarySettings }
        .distinctUntilChanged()

    private val _albumsResult: StateFlow<Result<List<Album>>> = _librarySettings
        .flatMapLatest { settings ->
            getAlbumsUseCase(
                sortOrder = settings.albumSortOrder,
                sortType = settings.albumSortType
            )
        }
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Result.Loading
        )

    val uiState: StateFlow<AlbumsUiState> = combine(
        _librarySettings,
        _albumsResult
    ) { settings, albumsResult ->
        AlbumsUiState(
            albums = if (albumsResult is Result.Success) albumsResult.data else emptyList(),
            gridSize = settings.albumGridSize,
            sortOrder = settings.albumSortOrder,
            sortType = settings.albumSortType,
            isLoading = albumsResult is Result.Loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AlbumsUiState(isLoading = true)
    )

    fun onEvent(event: AlbumsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is AlbumsUiEvent.GridSizeChanged -> updateAlbumsGridSizeUseCase(event.size)
                is AlbumsUiEvent.SortOrderChanged -> updateAlbumSortOrderUseCase(event.sortOrder)
                is AlbumsUiEvent.SortTypeChanged -> updateAlbumSortTypeUseCase(event.sortType)
            }
        }
    }
}