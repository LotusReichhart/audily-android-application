package com.lotusreichhart.audily.feature.songs.impl

import androidx.compose.runtime.Composable
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
import com.lotusreichhart.audily.feature.songs.impl.component.CompactSongs
import com.lotusreichhart.audily.feature.songs.impl.component.ExpandedSongs
import com.lotusreichhart.audily.feature.songs.impl.component.LandscapeSongs

/**
 * Điểm vào chính cho màn hình danh sách bài hát.
 * Chịu trách nhiệm quản lý trạng thái UI (Loading/Success) và chuyển đổi giao diện mượt mà.
 */

@Composable
internal fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isLoading = uiState.isLoading
    val songs = uiState.songs.collectAsLazyPagingItems()
    val summary = uiState.summary
    val sortOrder = uiState.sortOrder
    val sortType = uiState.sortType
    val playbackState = uiState.playbackState

    val screenState = rememberSongsScreenState()

    val globalUiEventBus = LocalGlobalUiEventBus.current

    SongsScreen(
        modifier = modifier,
        isLoading = isLoading,
        songs = songs,
        summary = summary,
        sortOrder = sortOrder,
        sortType = sortType,
        playbackState = playbackState,
        screenState = screenState,
        onEvent = viewModel::onEvent,
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
    songs: LazyPagingItems<Song>,
    summary: SongsSummary,
    sortOrder: SongSortOrder,
    sortType: SortOrderType,
    playbackState: PlaybackState,
    screenState: SongsScreenState,
    onEvent: (SongsUiEvent) -> Unit,
    onMenuClick: (song: Song) -> Unit
) {
    AudilyAdaptiveLayout(
        compact = {
            CompactSongs(
                modifier = modifier,
                isLoading = isLoading,
                songs = songs,
                summary = summary,
                sortOrder = sortOrder,
                sortType = sortType,
                playbackState = playbackState,
                screenState = screenState,
                onEvent = onEvent,
                onMenuClick = onMenuClick
            )
        },
        landscape = {
            LandscapeSongs(
                modifier = modifier,
                isLoading = isLoading,
                songs = songs,
                summary = summary,
                sortOrder = sortOrder,
                sortType = sortType,
                playbackState = playbackState,
                screenState = screenState,
                onEvent = onEvent,
                onMenuClick = onMenuClick
            )
        },
        expanded = {
            // Logic: Tablet/Fold sẽ dùng Landscape cho đến khi có Two-Pane đặc thù
            ExpandedSongs(
                modifier = modifier,
                isLoading = isLoading,
                songs = songs,
                summary = summary,
                sortOrder = sortOrder,
                sortType = sortType,
                playbackState = playbackState,
                screenState = screenState,
                onEvent = onEvent,
                onMenuClick = onMenuClick
            )
        }
    )
}
