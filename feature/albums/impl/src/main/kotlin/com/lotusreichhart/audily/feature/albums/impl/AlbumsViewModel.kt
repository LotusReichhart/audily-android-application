package com.lotusreichhart.audily.feature.albums.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.album.GetAlbumsUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAlbumGridSizeUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAlbumSortOrderUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateAlbumSortTypeUseCase
import com.lotusreichhart.audily.core.model.album.Album
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

    private val _albums: Flow<List<Album>> = combine(
        _librarySettings,
        _isDataLoadingStarted
    ) { settings, isStarted ->
        settings to isStarted
    }.flatMapLatest { (settings, isStarted) ->
        if (!isStarted) {
            flowOf(emptyList())
        } else {
            getAlbumsUseCase(
                sortOrder = settings.albumSortOrder,
                sortType = settings.albumSortType
            )
        }
    }

    val uiState: StateFlow<AlbumsUiState> = combine(
        _librarySettings,
        _albums,
        _isInitialLoading
    ) { settings, albums, isLoading ->
        AlbumsUiState(
            albums = albums,
            gridSize = settings.albumGridSize,
            sortOrder = settings.albumSortOrder,
            sortType = settings.albumSortType,
            isLoading = isLoading
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