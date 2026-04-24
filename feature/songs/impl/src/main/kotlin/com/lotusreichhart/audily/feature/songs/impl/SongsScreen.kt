package com.lotusreichhart.audily.feature.songs.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.lotusreichhart.audily.core.common.util.TimeUtils
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.component.AudilySortButton
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.component.SongPlaybackStatus
import com.lotusreichhart.audily.core.designsystem.icon.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.LocalDynamicBottomPadding
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongsSummary

@Composable
internal fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // TODO: Sprint 2.3 - Thay bằng thực thể từ Playback ViewModel/Manager (MediaController)
    val playingSongId by viewModel.playingSongId.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SongsUiState.Loading -> {
            AudilyScaffold(
                containerColor = Color.Transparent,
                modifier = modifier.fillMaxSize()
            ) { }
        }

        is SongsUiState.Success -> {
            val songs = state.songs.collectAsLazyPagingItems()
            SongsScreen(
                songs = songs,
                summary = state.summary,
                playingSongId = playingSongId,
                isPaused = isPaused,
                onEvent = { viewModel.onEvent(it) },
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SongsScreen(
    songs: LazyPagingItems<Song>,
    summary: SongsSummary,
    playingSongId: Long?,
    isPaused: Boolean,
    onEvent: (SongsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomPadding = LocalDynamicBottomPadding.current

    AudilyScaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                bottom = bottomPadding + LocalDimensions.current.paddingSmall
            )
        ) {
            item {
                SongsHeader(
                    songCount = summary.totalCount,
                    totalDuration = TimeUtils.formatDuration(summary.totalDuration),
                    sortText = "Title - Ascending",
                    onSortClick = {
                        // TODO: (Sprint 2.4) Mở Menu sắp xếp và gọi onEvent(SortOrderChanged)
                    }
                )
            }

            items(
                count = songs.itemCount,
                key = songs.itemKey { it.id },
                contentType = songs.itemContentType { "song" }
            ) { index ->
                val song = songs[index]
                if (song != null) {
                    val isCurrent = song.id == playingSongId
                    
                    val playbackStatus = when {
                        !isCurrent -> SongPlaybackStatus.NONE
                        isPaused -> SongPlaybackStatus.PAUSED
                        else -> SongPlaybackStatus.PLAYING
                    }
                    
                    SongItem(
                        title = song.basic.title,
                        artist = song.basic.artist,
                        albumArt = {
                            AsyncImage(
                                model = song.basic.artworkUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop
                            )
                        },
                        isMissing = song.isMissing,
                        playbackStatus = playbackStatus,
                        onClick = { onEvent(SongsUiEvent.SongClicked(song.id)) },
                        onMenuClick = {
                            // TODO: (Sprint 2.4) Show song options menu
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SongsHeader(
    songCount: Int,
    totalDuration: String,
    sortText: String,
    onSortClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = LocalDimensions.current.paddingSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingExtraSmall),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "$songCount songs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingExtraSmall)
            ) {
                Icon(
                    painter = painterResource(id = AudilyIcons.Timer),
                    contentDescription = "Timer",
                    modifier = Modifier.size(LocalDimensions.current.iconSizeSmall),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = totalDuration,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AudilySortButton(
            text = sortText,
            onClick = onSortClick
        )
    }
}
