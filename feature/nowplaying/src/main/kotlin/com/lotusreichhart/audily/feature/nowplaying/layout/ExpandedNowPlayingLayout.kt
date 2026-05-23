package com.lotusreichhart.audily.feature.nowplaying.layout

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiEvent
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiState
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingArtworkPager
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingControls
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingExtension
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingInfo
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingLyricsLoadingView
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingLyricsView
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingNoLyrics
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingProgress
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingTopBar
import com.lotusreichhart.audily.feature.nowplaying.queue.QueueScreen
import com.lotusreichhart.audily.feature.nowplaying.util.nowPlayingBackground

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ExpandedNowPlayingLayout(
    modifier: Modifier = Modifier,
    uiState: NowPlayingUiState,
    onMenuClick: () -> Unit,
    onLyricsToggle: () -> Unit,
    onExtendClick: () -> Unit,
    onTimerActiveClick: () -> Unit,
    onCloseClick: () -> Unit,
    onEvent: (NowPlayingUiEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope
) {
    AudilyScaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .nowPlayingBackground(uiState.paletteColors),
        containerColor = Color.Transparent,
        topBar = {
            NowPlayingTopBar(
                onCloseClick = onCloseClick,
                onMenuClick = onMenuClick
            )
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(0.6f))
                NowPlayingExtension(
                    modifier = Modifier
                        .weight(0.4f)
                        .padding(horizontal = LocalDimensions.current.paddingMedium)
                        .padding(bottom = LocalDimensions.current.paddingMedium),
                    isLyricsVisible = uiState.isLyricsVisible,
                    sleepTimerActive = uiState.sleepTimerStatus.isActive,
                    isOpenQueue = false,
                    onQueueClick = {},
                    onLyricsClick = onLyricsToggle,
                    onTimerClick = onTimerActiveClick,
                    onExtendClick = onExtendClick
                )
            }
        }
    ) { innerPadding ->
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QueueScreen(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight(),
                isExpanded = true
            )

            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
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
                            modifier = Modifier
                                .fillMaxWidth(),
                            artworkModifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = LocalDimensions.current.paddingMedium),
                            uiState = uiState,
                            onEvent = onEvent,
                            sharedTransitionScope = sharedTransitionScope
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    this.rotationY = 180f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isLyricsLoading) {
                                NowPlayingLyricsLoadingView()
                            } else if (uiState.lyrics != null) {
                                NowPlayingLyricsView(
                                    lyrics = uiState.lyrics,
                                    currentPositionMs = uiState.playbackPositionMs,
                                    onLineClick = { position -> onEvent(NowPlayingUiEvent.OnSeekTo(position)) }
                                )
                            } else {
                                NowPlayingNoLyrics()
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = LocalDimensions.current.paddingExtraSmall,
                        alignment = Alignment.CenterVertically
                    )
                ) {
                    NowPlayingInfo(
                        modifier = Modifier.padding(horizontal = LocalDimensions.current.paddingMedium),
                        title = uiState.currentSong?.basic?.title
                            ?: stringResource(coreR.string.core_designsystem_unknown_title),
                        artist = uiState.currentSong?.basic?.artist
                            ?: stringResource(coreR.string.core_designsystem_unknown_artist),
                        isFavorite = uiState.currentSong?.isFavorite ?: false,
                        onFavoriteClick = { onEvent(NowPlayingUiEvent.OnToggleFavorite) }
                    )
                    NowPlayingProgress(
                        modifier = Modifier.padding(horizontal = LocalDimensions.current.paddingMedium),
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

                    NowPlayingControls(
                        modifier = Modifier.padding(horizontal = LocalDimensions.current.paddingMedium),
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
                }
                Spacer(modifier = Modifier.height(LocalDimensions.current.paddingExtraSmall))
            }
        }
    }
}