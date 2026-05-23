package com.lotusreichhart.audily.feature.favorites.impl

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.util.getSongPlaybackStatus
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.ui.GlobalMenuCaller
import com.lotusreichhart.audily.core.ui.GlobalParams
import com.lotusreichhart.audily.core.ui.GlobalSheetKey
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.feature.favorites.impl.R as implR
import com.lotusreichhart.audily.feature.favorites.impl.component.FavoritesLoadingScreen
import com.lotusreichhart.audily.feature.favorites.impl.component.FavoritesMenuSheet
import com.lotusreichhart.audily.feature.favorites.impl.component.FavoritesTopBar
import com.lotusreichhart.audily.feature.favorites.impl.component.FavoriteSongSwipeItem

@Composable
internal fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val songs = uiState.songs.collectAsLazyPagingItems()
    var showMenu by remember { mutableStateOf(false) }
    var showClearConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading, songs.itemCount) {
        if (!uiState.isLoading && songs.itemCount == 0) {
            onBack()
        }
    }

    FavoritesScreen(
        modifier = modifier,
        uiState = uiState,
        songs = songs,
        uiEvent = viewModel::onEvent,
        onBack = onBack,
        onMenu = { showMenu = true }
    )

    if (showMenu) {
        FavoritesMenuSheet(
            onDismissRequest = { showMenu = false },
            onPlayAll = { viewModel.onEvent(FavoritesUiEvent.PlayAll) },
            onClearAll = {
                showClearConfirmationDialog = true
            }
        )
    }

    if (showClearConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmationDialog = false },
            title = {
                Text(
                    text = stringResource(implR.string.feature_favorites_impl_clear_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(implR.string.feature_favorites_impl_clear_confirm),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirmationDialog = false
                        viewModel.onEvent(FavoritesUiEvent.DeleteAll)
                    }
                ) {
                    Text(
                        text = stringResource(coreR.string.core_designsystem_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmationDialog = false }
                ) {
                    Text(
                        text = stringResource(coreR.string.core_designsystem_cancel),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun FavoritesScreen(
    modifier: Modifier = Modifier,
    uiState: FavoritesUiState,
    songs: LazyPagingItems<Song>,
    uiEvent: (FavoritesUiEvent) -> Unit,
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val globalUiEventBus = LocalGlobalUiEventBus.current

    AudilyScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FavoritesTopBar(
                onBack = onBack,
                onMenu = onMenu
            )
        }
    ) { innerPaddings ->
        if (uiState.isLoading) {
            FavoritesLoadingScreen(
                modifier = Modifier.padding(innerPaddings)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings)
            ) {
                items(
                    count = songs.itemCount,
                    key = songs.itemKey { it.id },
                    contentType = songs.itemContentType { "song" }
                ) { index ->
                    val song = songs[index]
                    if (song != null) {
                        val playbackStatus = getSongPlaybackStatus(song.id, uiState.playbackState)
                        FavoriteSongSwipeItem(
                            song = song,
                            playbackStatus = playbackStatus,
                            onEvent = uiEvent,
                            onMenuClick = { s ->
                                globalUiEventBus.emit(
                                    GlobalUiEvent.OpenSheet(
                                        key = GlobalSheetKey.SONG_MENU,
                                        params = mapOf(
                                            GlobalParams.PARAM_SONG to s,
                                            GlobalParams.PARAM_CALLER to GlobalMenuCaller.LIST_SCREEN,
                                            GlobalParams.PARAM_QUEUE_IDS to uiState.songIds
                                        ),
                                        isShowDragHandle = false
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}