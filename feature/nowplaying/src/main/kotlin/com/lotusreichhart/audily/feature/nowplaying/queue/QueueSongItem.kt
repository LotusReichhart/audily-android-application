package com.lotusreichhart.audily.feature.nowplaying.queue

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.component.SongPlaybackStatus
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QueueSongItem(
    modifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier,
    index: Int,
    song: Song,
    playbackStatus: SongPlaybackStatus,
    isDragging: Boolean = false,
    onEvent: (QueueUiEvent) -> Unit,
    onItemMenuClick: (index: Int, songId: Long) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
    )

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd,
            SwipeToDismissBoxValue.EndToStart -> {
                onEvent(QueueUiEvent.OnRemoveFromQueue(song.id))
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }

            else -> {}
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = !isDragging,
        enableDismissFromEndToStart = !isDragging,
        backgroundContent = { QueueSwipeBackground(dismissState) },
        content = {
            SongItem(
                modifier = modifier.background(Color.Transparent),
                title = song.basic.title,
                artist = song.basic.artist,
                albumArt = {
                    AudilyArtwork(
                        artworkUri = song.basic.artworkUri,
                        modifier = Modifier.fillMaxSize()
                    )
                },
                isMissing = song.isMissing,
                isFavorite = song.isFavorite,
                showDragHandle = true, // Quan trọng: Bật Drag Handle cho Queue
                dragHandleModifier = dragHandleModifier,
                playbackStatus = playbackStatus,
                onClick = { onEvent(QueueUiEvent.OnSongClicked(index, song.id)) },
                onMenuClick = { onItemMenuClick(index, song.id) },
                inNowPlaying = true
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QueueSwipeBackground(
    dismissState: SwipeToDismissBoxState
) {
    val dimensions = LocalDimensions.current
    val direction = dismissState.dismissDirection

    val actionConfig = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> SwipeActionConfig(
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            alignment = Alignment.CenterStart,
            icon = NowPlayingIcons.RemoveFromQueue,
            iconTint = MaterialTheme.colorScheme.onSurface
        )

        SwipeToDismissBoxValue.EndToStart -> SwipeActionConfig(
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            alignment = Alignment.CenterEnd,
            icon = NowPlayingIcons.RemoveFromQueue,
            iconTint = MaterialTheme.colorScheme.onSurface
        )

        else -> SwipeActionConfig(
            backgroundColor = Color.Transparent,
            alignment = Alignment.Center,
            icon = null,
            iconTint = Color.Transparent
        )
    }

    val color by animateColorAsState(actionConfig.backgroundColor, label = "color")
    val scale by animateFloatAsState(
        if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1.2f,
        label = "scale"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = dimensions.paddingLarge),
        contentAlignment = actionConfig.alignment
    ) {
        if (actionConfig.icon != null) {
            Icon(
                painter = painterResource(actionConfig.icon),
                contentDescription = "Delete",
                modifier = Modifier
                    .scale(scale)
                    .size(dimensions.iconSizeLarge),
                tint = actionConfig.iconTint
            )
        }
    }
}

private data class SwipeActionConfig(
    val backgroundColor: Color,
    val alignment: Alignment,
    val icon: Int?,
    val iconTint: Color
)
