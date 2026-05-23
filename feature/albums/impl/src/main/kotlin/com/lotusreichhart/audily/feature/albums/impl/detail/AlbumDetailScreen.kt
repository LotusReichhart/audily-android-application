package com.lotusreichhart.audily.feature.albums.impl.detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.lotusreichhart.audily.feature.albums.impl.detail.component.AlbumSavePlaylistSheet
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.ActionItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyActionSheet
import com.lotusreichhart.audily.core.designsystem.component.AudilyBottomSheet
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.albums.impl.R
import com.lotusreichhart.audily.core.ui.GlobalMenuCaller
import com.lotusreichhart.audily.core.ui.GlobalParams
import com.lotusreichhart.audily.core.ui.GlobalSheetKey
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.core.ui.adaptive.AudilyAdaptiveLayout
import com.lotusreichhart.audily.feature.albums.impl.detail.component.PortraitAlbumDetailLoadingScreen
import com.lotusreichhart.audily.feature.albums.impl.detail.component.LandscapeAlbumDetailLoadingScreen
import com.lotusreichhart.audily.feature.albums.impl.detail.layout.LandscapeAlbumDetailLayout
import com.lotusreichhart.audily.feature.albums.impl.detail.layout.PortraitAlbumDetailLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumDetailScreen(
    albumId: Long,
    onBack: () -> Unit,
    onNavigateToPlaylist: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val globalUiEventBus = LocalGlobalUiEventBus.current

    LaunchedEffect(albumId) {
        viewModel.onEvent(AlbumDetailUiEvent.Init(albumId))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AlbumDetailUiEffect.NavigateToPlaylist -> {
                    onNavigateToPlaylist(effect.playlistId)
                }
            }
        }
    }

    var showAlbumMenu by remember { mutableStateOf(false) }
    var showSavePlaylistSheet by remember { mutableStateOf(false) }

    AlbumDetailScreen(
        modifier = modifier,
        uiState = uiState,
        onBack = onBack,
        onMenuClick = {
            showAlbumMenu = true
        },
        onEvent = viewModel::onEvent,
        onSongMenuClick = { song ->
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    key = GlobalSheetKey.SONG_MENU,
                    params = mapOf(
                        GlobalParams.PARAM_SONG to song,
                        GlobalParams.PARAM_CALLER to GlobalMenuCaller.LIST_SCREEN,
                        GlobalParams.PARAM_QUEUE_IDS to uiState.songIds
                    ),
                    isShowDragHandle = false
                )
            )
        }
    )

    if (showAlbumMenu) {
        AudilyBottomSheet(
            onDismissRequest = { showAlbumMenu = false },
            isFullScreen = false,
            showDragHandle = false,
            enableSwipeToDismiss = true,
            containerColor = Color.Transparent
        ) {
            AudilyActionSheet(
                options = listOf(
                    ActionItem(
                        label = stringResource(R.string.feature_albums_impl_detail_save_as_playlist),
                        icon = AudilyIcons.Playlist,
                        onClick = {
                            showAlbumMenu = false
                            showSavePlaylistSheet = true
                        }
                    )
                ),
                onDismiss = { showAlbumMenu = false }
            )
        }
    }

    if (showSavePlaylistSheet) {
        val album = uiState.album
        val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant
        AudilyBottomSheet(
            onDismissRequest = { showSavePlaylistSheet = false },
            isFullScreen = false,
            showDragHandle = true,
            enableSwipeToDismiss = true,
            containerColor = sheetContainerColor,
            skipPartiallyExpanded = true
        ) {
            AlbumSavePlaylistSheet(
                initialName = album?.title ?: "",
                initialDescription = "",
                onDismiss = { showSavePlaylistSheet = false },
                onSave = { name, description ->
                    showSavePlaylistSheet = false
                    viewModel.onEvent(AlbumDetailUiEvent.OnSaveAsPlaylist(name, description))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumDetailScreen(
    uiState: AlbumDetailUiState,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onEvent: (AlbumDetailUiEvent) -> Unit,
    onSongMenuClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    AudilyAdaptiveLayout(
        portrait = {
            if (uiState.isLoading) {
                PortraitAlbumDetailLoadingScreen()
            } else {
                PortraitAlbumDetailLayout(
                    uiState = uiState,
                    onBack = onBack,
                    onMenuClick = onMenuClick,
                    onEvent = onEvent,
                    onSongMenuClick = onSongMenuClick,
                    modifier = modifier
                )
            }
        },
        landscape = {
            if (uiState.isLoading) {
                LandscapeAlbumDetailLoadingScreen(
                    onBack = onBack,
                    modifier = modifier
                )
            } else {
                LandscapeAlbumDetailLayout(
                    uiState = uiState,
                    onBack = onBack,
                    onMenuClick = onMenuClick,
                    onEvent = onEvent,
                    onSongMenuClick = onSongMenuClick,
                    modifier = modifier
                )
            }
        },
        medium = {
            if (uiState.isLoading) {
                PortraitAlbumDetailLoadingScreen()
            } else {
                PortraitAlbumDetailLayout(
                    uiState = uiState,
                    onBack = onBack,
                    onMenuClick = onMenuClick,
                    onEvent = onEvent,
                    onSongMenuClick = onSongMenuClick,
                    modifier = modifier
                )
            }
        },
        expanded = {
            if (uiState.isLoading) {
                PortraitAlbumDetailLoadingScreen()
            } else {
                PortraitAlbumDetailLayout(
                    uiState = uiState,
                    onBack = onBack,
                    onMenuClick = onMenuClick,
                    onEvent = onEvent,
                    onSongMenuClick = onSongMenuClick,
                    modifier = modifier
                )
            }
        }
    )
}