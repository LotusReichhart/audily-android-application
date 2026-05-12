package com.lotusreichhart.audily.feature.songs.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import com.lotusreichhart.audily.core.ui.GlobalMenuCaller
import com.lotusreichhart.audily.core.ui.GlobalParams
import com.lotusreichhart.audily.core.ui.GlobalSheetKey
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.core.ui.adaptive.AudilyAdaptiveLayout
import androidx.compose.material3.MaterialTheme
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.feature.songs.impl.layout.PortraitSongsLayout
import com.lotusreichhart.audily.feature.songs.impl.layout.ExpandedSongsLayout
import com.lotusreichhart.audily.feature.songs.impl.layout.LandscapeSongsLayout
import com.lotusreichhart.audily.feature.songs.impl.component.SongsSortSheet
import com.lotusreichhart.audily.feature.songs.impl.layout.MediumSongsLayout

/**
 * Điểm vào chính cho màn hình danh sách bài hát.
 * Chịu trách nhiệm quản lý trạng thái UI (Loading/Success) và chuyển đổi giao diện mượt mà.
 */

@Composable
internal fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSearch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isLoading = uiState.isLoading
    val isRefreshing = uiState.isRefreshing
    val wasRefreshed = uiState.wasRefreshed
    val songs = uiState.songs.collectAsLazyPagingItems()
    val summary = uiState.summary
    val sortOrder = uiState.sortOrder
    val sortType = uiState.sortType
    val playbackState = uiState.playbackState

    val screenState = rememberSongsScreenState()
    val globalUiEventBus = LocalGlobalUiEventBus.current
    val sheetController = LocalAudilySheetController.current
    val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant

    // Tự động cuộn lên đầu khi thay đổi sắp xếp
    LaunchedEffect(sortOrder, sortType) {
        screenState.lazyListState.animateScrollToItem(0)
    }

    SongsScreen(
        modifier = modifier,
        isLoading = isLoading,
        isRefreshing = isRefreshing,
        wasRefreshed = wasRefreshed,
        songs = songs,
        summary = summary,
        sortOrder = sortOrder,
        sortType = sortType,
        playbackState = playbackState,
        screenState = screenState,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        onSearchClick = onSearch,
        onSortClick = {
            sheetController.showSheet(
                content = {
                    SongsSortSheet(
                        initialSortOrder = sortOrder,
                        initialSortType = sortType,
                        onSave = { order, type ->
                            viewModel.onEvent(SongsUiEvent.SortOrderChanged(order))
                            viewModel.onEvent(SongsUiEvent.SortTypeChanged(type))
                            sheetController.hideSheet()
                        }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        },
        onMenuClick = { song ->
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    key = GlobalSheetKey.SONG_MENU,
                    params = mapOf(
                        GlobalParams.PARAM_SONG to song,
                        GlobalParams.PARAM_CALLER to GlobalMenuCaller.LIST_SCREEN,
                        GlobalParams.PARAM_QUEUE_IDS to uiState.allSongIds
                    ),
                    isShowDragHandle = false
                )
            )
        }
    )
}

@Composable
internal fun SongsScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    isRefreshing: Boolean = false,
    wasRefreshed: Boolean = false,
    songs: LazyPagingItems<Song>,
    summary: SongsSummary,
    sortOrder: SongSortOrder,
    sortType: SortOrderType,
    playbackState: PlaybackState,
    screenState: SongsScreenState,
    onEvent: (SongsUiEvent) -> Unit,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onMenuClick: (song: Song) -> Unit
) {
    AudilyAdaptiveLayout(
        portrait = {
            PortraitSongsLayout(
                modifier = modifier,
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                wasRefreshed = wasRefreshed,
                songs = songs,
                summary = summary,
                sortOrder = sortOrder,
                sortType = sortType,
                playbackState = playbackState,
                screenState = screenState,
                onBack = onBack,
                onSearchClick = onSearchClick,
                onSortClick = onSortClick,
                onEvent = onEvent,
                onMenuClick = onMenuClick,
            )
        },
        landscape = {
            LandscapeSongsLayout(
                modifier = modifier,
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                wasRefreshed = wasRefreshed,
                songs = songs,
                summary = summary,
                sortOrder = sortOrder,
                sortType = sortType,
                playbackState = playbackState,
                screenState = screenState,
                onBack = onBack,
                onSearchClick = onSearchClick,
                onSortClick = onSortClick,
                onEvent = onEvent,
                onMenuClick = onMenuClick
            )
        },
        medium = {
            MediumSongsLayout(
                modifier = modifier,
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                wasRefreshed = wasRefreshed,
                songs = songs,
                summary = summary,
                sortOrder = sortOrder,
                sortType = sortType,
                playbackState = playbackState,
                screenState = screenState,
                onBack = onBack,
                onSearchClick = onSearchClick,
                onSortClick = onSortClick,
                onEvent = onEvent,
                onMenuClick = onMenuClick
            )
        },
        expanded = {
            ExpandedSongsLayout(
                modifier = modifier,
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                wasRefreshed = wasRefreshed,
                songs = songs,
                summary = summary,
                sortOrder = sortOrder,
                sortType = sortType,
                playbackState = playbackState,
                screenState = screenState,
                onBack = onBack,
                onSearchClick = onSearchClick,
                onSortClick = onSortClick,
                onEvent = onEvent,
                onMenuClick = onMenuClick
            )
        }
    )
}
