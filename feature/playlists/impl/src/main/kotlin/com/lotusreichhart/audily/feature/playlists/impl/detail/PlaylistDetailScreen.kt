package com.lotusreichhart.audily.feature.playlists.impl.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.component.PlaylistGridCover
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.getSongPlaybackStatus
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.ui.GlobalMenuCaller
import com.lotusreichhart.audily.core.ui.GlobalParams
import com.lotusreichhart.audily.core.ui.GlobalSheetKey
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.feature.playlists.impl.R
import com.lotusreichhart.audily.feature.playlists.impl.component.PlaylistsAddOrUpdateSheet
import com.lotusreichhart.audily.feature.playlists.impl.detail.component.PlaylistDetailEmptyContent
import com.lotusreichhart.audily.feature.playlists.impl.detail.component.PlaylistDetailLoadingScreen
import com.lotusreichhart.audily.feature.playlists.impl.detail.component.PlaylistDetailTopBar
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun PlaylistDetailScreen(
    playlistId: Long,
    onBack: () -> Unit,
    onNavigateToAddSongs: (playlistId: Long) -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val globalUiEventBus = LocalGlobalUiEventBus.current
    val sheetController = LocalAudilySheetController.current
    val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(playlistId) {
        viewModel.onEvent(PlaylistDetailUiEvent.Init(playlistId))
    }

    PlaylistDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onEvent = viewModel::onEvent,
        onEditClicked = {
            sheetController.showSheet(
                content = {
                    PlaylistsAddOrUpdateSheet(
                        initialName = uiState.playlist?.name ?: "",
                        initialDescription = uiState.playlist?.description,
                        titleRes = R.string.feature_playlists_impl_edit_playlist,
                        buttonRes = coreR.string.core_designsystem_save,
                        onDismiss = { sheetController.hideSheet() },
                        onSave = { name, desc ->
                            viewModel.onEvent(PlaylistDetailUiEvent.EditMetadata(name, desc))
                            sheetController.hideSheet()
                        }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        },
        onDeleteClicked = {
            showDeleteDialog = true
        },
        onSongMenuClick = { song ->
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    key = GlobalSheetKey.SONG_MENU,
                    params = mapOf(
                        GlobalParams.PARAM_SONG to song,
                        GlobalParams.PARAM_PLAYLIST_ID to playlistId,
                        GlobalParams.PARAM_CALLER to GlobalMenuCaller.PLAYLIST,
                        GlobalParams.PARAM_QUEUE_IDS to uiState.songIds
                    ),
                    isShowDragHandle = false
                )
            )
        },
        onNavigateToAddSongs = {
            uiState.playlist?.id?.let { onNavigateToAddSongs(it) }
        }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.feature_playlists_impl_delete_playlist),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.feature_playlists_impl_delete_playlist_confirm),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.onEvent(PlaylistDetailUiEvent.DeletePlaylist)
                        onBack()
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
                    onClick = { showDeleteDialog = false }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun PlaylistDetailScreen(
    uiState: PlaylistDetailUiState,
    onBack: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onEvent: (PlaylistDetailUiEvent) -> Unit,
    onSongMenuClick: (Song) -> Unit,
    onNavigateToAddSongs: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val dimensions = LocalDimensions.current


    data class SongWrapper(
        val song: Song,
        val stableId: String = UUID.randomUUID().toString()
    )

    var localSongsWrappers by remember { mutableStateOf(uiState.songs.map { SongWrapper(it) }) }

    LaunchedEffect(uiState.songs) {
        val currentSongs = localSongsWrappers.map { it.song }
        if (currentSongs != uiState.songs) {
            localSongsWrappers = uiState.songs.map { song ->
                localSongsWrappers.find { it.song == song } ?: SongWrapper(song)
            }
        }
    }

    var initialIndex by remember { mutableStateOf<Int?>(null) }

    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = (from.index - 2).coerceIn(0, localSongsWrappers.lastIndex)
        val toIndex = (to.index - 2).coerceIn(0, localSongsWrappers.lastIndex)
        localSongsWrappers = localSongsWrappers.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
    }

    AudilyScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            PlaylistDetailTopBar(
                songsSummary = uiState.songsSummary,
                isLoading = uiState.isLoading,
                onBack = onBack,
                onEdit = onEditClicked,
                onDelete = onDeleteClicked
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.padding(paddingValues)) {
                PlaylistDetailLoadingScreen()
            }
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    PlaylistDetailHeaderInfo(
                        playlistName = uiState.playlist?.name
                            ?: stringResource(R.string.feature_playlists_impl_unknown_playlist),
                        description = uiState.playlist?.description,
                        artworkUris = uiState.playlist?.artworkUris ?: emptyList()
                    )
                }

                stickyHeader {
                    PlaylistDetailStickyActions(
                        onPlayClick = { onEvent(PlaylistDetailUiEvent.PlayAll) },
                        onNavigateToAddSongs = onNavigateToAddSongs
                    )
                }

                if (uiState.songs.isEmpty()) {
                    item {
                        PlaylistDetailEmptyContent(
                            onAddSongsClick = onNavigateToAddSongs,
                            modifier = Modifier.fillParentMaxHeight(0.5f)
                        )
                    }
                } else {
                    itemsIndexed(
                        items = localSongsWrappers,
                        key = { _, wrapper -> wrapper.stableId }
                    ) { index, wrapper ->
                        val song = wrapper.song
                        val dismissState = rememberSwipeToDismissBoxState(
                            initialValue = SwipeToDismissBoxValue.Settled
                        )

                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                                onEvent(PlaylistDetailUiEvent.RemoveSong(song.id))
                                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = true,
                            enableDismissFromEndToStart = false,
                            backgroundContent = {
                                val isSwiping =
                                    dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = dimensions.paddingMedium)
                                        .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                                        .background(Color.Transparent),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (isSwiping) {
                                        Icon(
                                            painter = painterResource(id = AudilyIcons.Delete),
                                            contentDescription = "Delete",
                                            modifier = Modifier
                                                .padding(start = dimensions.paddingMedium)
                                                .size(24.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        ) {
                            ReorderableItem(
                                reorderableState,
                                key = wrapper.stableId
                            ) { isDragging ->
                                val elevation by animateFloatAsState(
                                    if (isDragging) 8f else 0f,
                                    label = "elevation"
                                )

                                val onDragStarted = { initialIndex = index }
                                val onDragStopped = {
                                    val from = initialIndex
                                    val to =
                                        localSongsWrappers.indexOfFirst { it.stableId == wrapper.stableId }
                                    if (from != null && to != -1 && from != to) {
                                        onEvent(PlaylistDetailUiEvent.ReorderSongs(from, to))
                                    }
                                    initialIndex = null
                                }

                                val playbackStatus =
                                    getSongPlaybackStatus(song.id, uiState.playbackState)

                                SongItem(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                        .zIndex(if (isDragging) 1f else 0f)
                                        .graphicsLayer { shadowElevation = elevation }
                                        .longPressDraggableHandle(
                                            onDragStarted = { onDragStarted() },
                                            onDragStopped = { onDragStopped() }
                                        ),
                                    title = song.basic.title,
                                    artist = song.basic.artist,
                                    albumArt = {
                                        AudilyArtwork(
                                            artworkUri = song.basic.artworkUri,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    },
                                    onClick = {
                                        onEvent(
                                            PlaylistDetailUiEvent.SongClicked(
                                                song.id,
                                                song.isMissing
                                            )
                                        )
                                    },
                                    onMenuClick = { onSongMenuClick(song) },
                                    isMissing = song.isMissing,
                                    showDragHandle = true,
                                    dragHandleModifier = Modifier.draggableHandle(
                                        onDragStarted = { onDragStarted() },
                                        onDragStopped = { onDragStopped() }
                                    ),
                                    playbackStatus = playbackStatus
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistDetailHeaderInfo(
    playlistName: String,
    description: String?,
    artworkUris: List<String?>,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium)
            .padding(top = dimensions.paddingMedium, bottom = dimensions.paddingSmall),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
    ) {
        PlaylistGridCover(
            artworkUris = artworkUris,
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingExtraSmall)
        ) {
            Text(
                text = playlistName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaylistDetailStickyActions(
    onPlayClick: () -> Unit,
    onNavigateToAddSongs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = dimensions.paddingMedium)
            .padding(vertical = dimensions.paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AudilyButton(
            text = stringResource(R.string.feature_playlists_impl_playlist_play),
            onClick = onPlayClick,
            modifier = Modifier.weight(1f),
            leadingIcon = AudilyIcons.Resume
        )

        AudilyIconButton(
            onClick = onNavigateToAddSongs,
            painter = painterResource(id = AudilyIcons.Add),
            contentDescription = "Add",
            iconSize = 24.dp,
            containerSize = dimensions.buttonHeight,
            backgroundColor = MaterialTheme.colorScheme.onBackground,
            tint = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
        )
    }
}
