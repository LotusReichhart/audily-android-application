package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceVariantDark
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiEvent
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiState
import com.lotusreichhart.audily.feature.nowplaying.queue.QueueScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ExpandedNowPlaying(
    modifier: Modifier = Modifier,
    uiState: NowPlayingUiState,
    onLyricsToggle: () -> Unit,
    onMenuToggle: () -> Unit,
    onCloseClick: () -> Unit,
    onOpenQueue: () -> Unit,
    onEvent: (NowPlayingUiEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.Transparent,
        topBar = {
            NowPlayingHeader(
                modifier = modifier
                    .padding(horizontal = LocalDimensions.current.paddingMedium)
                    .padding(bottom = LocalDimensions.current.paddingMedium),
                onCloseClick = onCloseClick,
                onMenuClick = onMenuToggle
            )
        },
    ) { paddingValues ->
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = LocalDimensions.current.paddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bên trái (2/5): Queue
            QueueScreen(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            )

            Spacer(modifier = Modifier.width(LocalDimensions.current.paddingExtraLarge))

            // Bên phải (3/5): Giao diện Player
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
                    .padding(vertical = LocalDimensions.current.paddingMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Artwork & Lyrics Flip
                val rotationY by animateFloatAsState(
                    targetValue = if (uiState.isLyricsVisible) 180f else 0f,
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    ),
                    label = "CardFlip"
                )
                val density = LocalDensity.current

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer {
                            this.rotationY = rotationY
                            cameraDistance = 12f * density.density
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (rotationY <= 90f) {
                        // Mặt trước: Artwork
                        NowPlayingArtworkPager(
                            modifier = Modifier.fillMaxSize(),
                            artworkModifier = Modifier
                                .fillMaxHeight(0.9f)
                                .fillMaxWidth(0.9f),
                            uiState = uiState,
                            onEvent = onEvent,
                            sharedTransitionScope = sharedTransitionScope
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    this.rotationY = 180f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            NowPlayingNoLyrics()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(LocalDimensions.current.paddingLarge))

                // Info, Progress, Controls
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NowPlayingInfo(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantDark.copy(alpha = 0.7f))
                            .padding(LocalDimensions.current.paddingMedium),
                        title = uiState.currentSong?.basic?.title
                            ?: stringResource(coreR.string.core_designsystem_unknown_title),
                        artist = uiState.currentSong?.basic?.artist
                            ?: stringResource(coreR.string.core_designsystem_unknown_artist),
                        isFavorite = uiState.currentSong?.isFavorite ?: false,
                        onFavoriteClick = { onEvent(NowPlayingUiEvent.OnToggleFavorite) }
                    )

                    Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))

                    NowPlayingProgress(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        progressMs = uiState.playbackPositionMs,
                        durationMs = uiState.playbackState.duration,
                        songId = uiState.currentSong?.id,
                        onSeek = { position ->
                            if (uiState.playbackState.duration in 1..position) {
                                onEvent(NowPlayingUiEvent.OnSkipNext)
                            } else {
                                onEvent(NowPlayingUiEvent.OnSeekTo(position))
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.paddingLarge))
                    NowPlayingControls(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        isPlaying = uiState.playbackState.nowPlayingState == NowPlayingState.PLAYING,
                        isShuffleOn = uiState.playbackState.isShuffleOn,
                        repeatMode = uiState.playbackState.repeatMode,
                        onResumePauseClick = { onEvent(NowPlayingUiEvent.OnResumePauseToggle) },
                        onSkipNextClick = { onEvent(NowPlayingUiEvent.OnSkipNext) },
                        onSkipPreviousClick = { onEvent(NowPlayingUiEvent.OnSkipPrevious) },
                        onFastForwardClick = { onEvent(NowPlayingUiEvent.OnFastForward) },
                        onFastRewindClick = { onEvent(NowPlayingUiEvent.OnFastRewind) },
                        onShuffleClick = { onEvent(NowPlayingUiEvent.OnShuffleToggle) },
                        onRepeatClick = { onEvent(NowPlayingUiEvent.OnRepeatModeToggle) },
                        hasNext = uiState.hasNext,
                        hasPrevious = uiState.hasPrevious
                    )

                    Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))

                    NowPlayingExtension(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        isLyricsVisible = uiState.isLyricsVisible,
                        onQueueClick = onOpenQueue,
                        onLyricsClick = onLyricsToggle,
                        onExtendClick = onMenuToggle
                    )
                }
            }
        }
    }
}