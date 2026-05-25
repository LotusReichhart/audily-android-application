package com.lotusreichhart.audily.feature.nowplaying.queue

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.model.song.Song
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.ActionItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyActionSheet
import com.lotusreichhart.audily.core.designsystem.component.AudilyBottomSheet
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.component.SongPlaybackStatus
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.feature.nowplaying.R
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons
import com.lotusreichhart.audily.feature.nowplaying.util.nowPlayingBackground
import sh.calvin.reorderable.ReorderableItem
import com.lotusreichhart.audily.core.navigation.LocalNavigator
import com.lotusreichhart.audily.feature.nowplaying.queue.component.QueueSavePlaylistSheet
import com.lotusreichhart.audily.feature.nowplaying.queue.component.QueueSongItem
import com.lotusreichhart.audily.feature.nowplaying.queue.component.QueueTopBar
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistDetailNavKey
import kotlinx.coroutines.flow.collectLatest
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QueueScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    isExpanded: Boolean = false,
    viewModel: QueueViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    var showQueueMenu by remember { mutableStateOf(false) }
    var selectedIndexForMenu by remember { mutableStateOf<Int?>(null) }
    var selectedSongForMenu by remember { mutableStateOf<Song?>(null) }
    var showSongMenu by remember { mutableStateOf(false) }
    var showSavePlaylistSheet by remember { mutableStateOf(false) }

    val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant
    val navigator = LocalNavigator.current

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is QueueUiEffect.NavigateToPlaylist -> {
                    showSavePlaylistSheet = false
                    onClose() // Closes the QueueScreen sheet
                    navigator.navigate(PlaylistDetailNavKey(effect.playlistId))
                }
            }
        }
    }

    LaunchedEffect(uiState.queue, uiState.playbackState.isInitialized) {
        if (uiState.playbackState.isInitialized && uiState.queue.isEmpty()) {
            onClose()
        }
    }

    QueueScreen(
        modifier = modifier,
        uiState = uiState,
        isExpanded = isExpanded,
        onEvent = viewModel::onEvent,
        onClose = onClose,
        onQueueMenuClick = { showQueueMenu = true },
        onItemMenuClick = { index, songId ->
            selectedSongForMenu = uiState.queue.find { it.id == songId }
            selectedIndexForMenu = index
            showSongMenu = true
        }
    )

    if (showQueueMenu) {
        AudilyBottomSheet(
            onDismissRequest = { showQueueMenu = false },
            isFullScreen = false,
            showDragHandle = false,
            enableSwipeToDismiss = true,
            containerColor = Color.Transparent
        ) {
            AudilyActionSheet(
                options = listOf(
                    ActionItem(
                        label = stringResource(R.string.feature_nowplaying_queue_save_as_playlist),
                        icon = AudilyIcons.Playlist,
                        onClick = {
                            showQueueMenu = false
                            showSavePlaylistSheet = true
                        }
                    ),
                    ActionItem(
                        label = stringResource(R.string.feature_nowplaying_queue_stop),
                        icon = NowPlayingIcons.Stop,
                        isDestructive = true,
                        onClick = {
                            viewModel.onEvent(QueueUiEvent.OnStopQueue)
                            onClose()
                        }
                    )
                ),
                onDismiss = { showQueueMenu = false }
            )
        }
    }

    if (showSavePlaylistSheet) {
        AudilyBottomSheet(
            onDismissRequest = { showSavePlaylistSheet = false },
            isFullScreen = false,
            showDragHandle = true,
            enableSwipeToDismiss = true,
            containerColor = sheetContainerColor,
            skipPartiallyExpanded = true
        ) {
            QueueSavePlaylistSheet(
                onDismiss = { showSavePlaylistSheet = false },
                onSave = { name, description ->
                    viewModel.onEvent(
                        QueueUiEvent.OnSaveQueueAsPlaylist(
                            name,
                            description
                        )
                    )
                }
            )
        }
    }

    val selectedSong = selectedSongForMenu
    val selectedIndex = selectedIndexForMenu

    if (showSongMenu && selectedSong != null && selectedIndex != null) {
        AudilyBottomSheet(
            onDismissRequest = { showSongMenu = false },
            isFullScreen = false,
            showDragHandle = false,
            enableSwipeToDismiss = true,
            containerColor = Color.Transparent
        ) {
            AudilyActionSheet(
                header = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AudilyArtwork(
                            artworkUri = selectedSong.basic.artworkUri,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedSong.basic.title,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = selectedSong.basic.artist,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                options = listOf(
                    ActionItem(
                        label = stringResource(R.string.feature_nowplaying_queue_play_now),
                        icon = AudilyIcons.Resume,
                        onClick = {
                            viewModel.onEvent(QueueUiEvent.OnSkipToIndex(selectedIndex))
                            showSongMenu = false
                        }
                    ),
                    ActionItem(
                        label = stringResource(R.string.feature_nowplaying_queue_remove_from_queue),
                        icon = AudilyIcons.QueueMusic,
                        isDestructive = true,
                        onClick = {
                            viewModel.onEvent(QueueUiEvent.OnRemoveFromQueue(selectedSong.id))
                            showSongMenu = false
                        }
                    )
                ),
                onDismiss = { showSongMenu = false }
            )
        }
    }
}

@Composable
internal fun QueueScreen(
    modifier: Modifier = Modifier,
    uiState: QueueUiState,
    isExpanded: Boolean = true,
    onEvent: (QueueUiEvent) -> Unit,
    onClose: () -> Unit,
    onQueueMenuClick: () -> Unit,
    onItemMenuClick: (index: Int, songId: Long) -> Unit
) {
    val lazyListState = rememberLazyListState()

    // Bọc song với một stableId duy nhất để handle bài hát trùng ID và đảm bảo kéo thả ổn định
    data class QueueWrapper(
        val song: Song,
        val stableId: String = UUID.randomUUID().toString()
    )

    // Local state để render list lập tức khi drag (tránh độ trễ từ ViewModel)
    var localQueueWrappers by remember { mutableStateOf(uiState.queue.map { QueueWrapper(it) }) }

    LaunchedEffect(uiState.queue) {
        // Chỉ cập nhật từ state gốc nếu dữ liệu thực tế thay đổi (ví dụ: chuyển bài, xóa bài)
        // So sánh theo ID bài hát để tránh reset stableId khi chỉ là reorder nội bộ
        val currentSongs = localQueueWrappers.map { it.song }
        if (currentSongs != uiState.queue) {
            localQueueWrappers = uiState.queue.map { song ->
                // Thử tìm lại wrapper cũ để giữ stableId
                localQueueWrappers.find { it.song == song } ?: QueueWrapper(song)
            }
        }
    }

    // Lưu lại vị trí bắt đầu khi bắt đầu kéo
    var initialIndex by remember { mutableStateOf<Int?>(null) }

    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localQueueWrappers = localQueueWrappers.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    AudilyScaffold(
        modifier = modifier.fillMaxWidth(),
        containerColor = if (isExpanded) Color.Transparent else MaterialTheme.colorScheme.background,
        topBar = {
            QueueTopBar(
                isExpanded = isExpanded,
                queueSummary = uiState.queueSummary,
                onCloseClick = onClose,
                onMenuClick = onQueueMenuClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = if (isExpanded) Modifier
                .padding(paddingValues)
            else Modifier
                .padding(paddingValues)
                .nowPlayingBackground(uiState.paletteColors, uiState.useGlassmorphism)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = localQueueWrappers,
                    key = { _, wrapper -> wrapper.stableId }
                ) { index, wrapper ->
                    val song = wrapper.song
                    ReorderableItem(reorderableState, key = wrapper.stableId) { isDragging ->
                        // Hiệu ứng nhấc lên khi đang drag
                        val elevation by animateFloatAsState(
                            if (isDragging) 8f else 0f,
                            label = "elevation"
                        )

                        val playbackStatus = when {
                            uiState.currentIndex == index -> {
                                if (uiState.playbackState.nowPlayingState == NowPlayingState.PLAYING)
                                    SongPlaybackStatus.PLAYING
                                else
                                    SongPlaybackStatus.PAUSED
                            }

                            else -> SongPlaybackStatus.NONE
                        }

                        val onDragStarted = {
                            initialIndex = index
                        }
                        val onDragStopped = {
                            val from = initialIndex
                            val to =
                                localQueueWrappers.indexOfFirst { it.stableId == wrapper.stableId }
                            if (from != null && to != -1 && from != to) {
                                onEvent(QueueUiEvent.OnMoveQueueItem(from, to))
                            }
                            initialIndex = null
                        }

                        QueueSongItem(
                            modifier = Modifier
                                .zIndex(if (isDragging) 1f else 0f)
                                .graphicsLayer {
                                    this.shadowElevation = elevation
                                }
                                .longPressDraggableHandle(
                                    onDragStarted = { onDragStarted() },
                                    onDragStopped = { onDragStopped() }
                                ),
                            index = index,
                            song = song,
                            playbackStatus = playbackStatus,
                            isDragging = isDragging,
                            dragHandleModifier = Modifier.draggableHandle(
                                onDragStarted = { onDragStarted() },
                                onDragStopped = { onDragStopped() }
                            ),
                            onEvent = onEvent,
                            onItemMenuClick = onItemMenuClick
                        )
                    }
                }
            }
        }
    }
}
