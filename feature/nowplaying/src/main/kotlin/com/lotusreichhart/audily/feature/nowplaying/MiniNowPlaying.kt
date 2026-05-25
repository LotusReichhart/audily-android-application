package com.lotusreichhart.audily.feature.nowplaying

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceDark
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.feature.nowplaying.component.MiniPlayerContent
import com.lotusreichhart.audily.feature.nowplaying.queue.QueueScreen

@Composable
fun MiniNowPlaying(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NowPlayingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetController = LocalAudilySheetController.current

    MiniNowPlaying(
        modifier = modifier,
        uiState = uiState,
        onClick = onClick,
        onOpenQueue = {
            sheetController.showSheet(
                content = {
                    QueueScreen(
                        onClose = { sheetController.hideSheet() }
                    )
                },
                isFullScreen = true,
                showDragHandle = false,
                enableSwipeToDismiss = false
            )
        },
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun MiniNowPlaying(
    modifier: Modifier = Modifier,
    uiState: NowPlayingUiState,
    onClick: () -> Unit,
    onOpenQueue: () -> Unit,
    onEvent: (NowPlayingUiEvent) -> Unit,
) {
    val useGlassmorphism = uiState.useGlassmorphism

    val defaultColor = SurfaceDark
    val vibrantColor by animateColorAsState(
        targetValue = uiState.paletteColors?.vibrant ?: defaultColor,
        animationSpec = tween(1000),
        label = "MiniVibrantAnimation"
    )
    val dominantColor by animateColorAsState(
        targetValue = uiState.paletteColors?.dominant ?: defaultColor,
        animationSpec = tween(1000),
        label = "MiniDominantAnimation"
    )

    val backgroundBrush = remember(vibrantColor, dominantColor, useGlassmorphism) {
        if (useGlassmorphism) {
            Brush.horizontalGradient(
                colors = listOf(
                    vibrantColor.copy(alpha = 0.4f),
                    dominantColor.copy(alpha = 0.35f)
                )
            )
        } else {
            null
        }
    }

    val progress = if (uiState.playbackState.duration > 0) {
        uiState.playbackPositionMs.toFloat() / uiState.playbackState.duration
    } else 0f

    MiniPlayerContent(
        modifier = modifier.background(SurfaceDark),
        backgroundBrush = backgroundBrush,
        title = uiState.currentSong?.basic?.title ?: "Unknown Title",
        artist = uiState.currentSong?.basic?.artist ?: "Unknown Artist",
        artworkUri = uiState.currentSong?.basic?.artworkUri,
        isPlaying = uiState.playbackState.nowPlayingState == NowPlayingState.PLAYING,
        progress = progress,
        onResumePauseClick = { onEvent(NowPlayingUiEvent.OnResumePauseToggle) },
        onNextClick = { onEvent(NowPlayingUiEvent.OnSkipNext) },
        onQueueClick = onOpenQueue,
        onClick = onClick,
        hasNext = uiState.hasNext
    )
}
