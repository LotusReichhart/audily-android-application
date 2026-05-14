package com.lotusreichhart.audily.feature.nowplaying.layout

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceVariantDark
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiEvent
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiState
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingArtworkPager
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingControls
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingExtension
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingInfo
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingNoLyrics
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingProgress
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingTopBar
import com.lotusreichhart.audily.feature.nowplaying.constants.SharedContentStateConstants
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
                Spacer(modifier = Modifier.weight(0.55f))
                NowPlayingExtension(
                    modifier = Modifier
                        .weight(0.45f)
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
                    .weight(0.55f)
                    .fillMaxHeight(),
                isExpanded = true
            )

            Column(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxWidth()
                ) {
                    with(sharedTransitionScope) {
                        AnimatedContent(
                            targetState = uiState.isLyricsVisible,
                            transitionSpec = {
                                fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                            },
                            label = "LyricsTransition"
                        ) { lyricsVisible ->
                            if (lyricsVisible) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = LocalDimensions.current.paddingMedium)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SurfaceVariantDark.copy(alpha = 0.7f))
                                            .padding(LocalDimensions.current.paddingSmall),
                                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingSmall),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        AudilyArtwork(
                                            artworkUri = uiState.currentSong?.basic?.artworkUri,
                                            modifier = Modifier
                                                .sharedElement(
                                                    rememberSharedContentState(
                                                        key = SharedContentStateConstants.KEY_ARTWORK
                                                    ),
                                                    animatedVisibilityScope = this@AnimatedContent
                                                )
                                                .size(52.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                        NowPlayingInfo(
                                            title = uiState.currentSong?.basic?.title
                                                ?: stringResource(coreR.string.core_designsystem_unknown_title),
                                            artist = uiState.currentSong?.basic?.artist
                                                ?: stringResource(coreR.string.core_designsystem_unknown_artist),
                                            isFavorite = uiState.currentSong?.isFavorite ?: false,
                                            onFavoriteClick = { onEvent(NowPlayingUiEvent.OnToggleFavorite) }
                                        )
                                    }
                                    NowPlayingNoLyrics(modifier = Modifier.fillMaxWidth())
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(
                                        space = LocalDimensions.current.paddingSmall,
                                        alignment = Alignment.CenterVertically
                                    )
                                ) {
                                    NowPlayingArtworkPager(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        artworkModifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = LocalDimensions.current.paddingMedium),
                                        uiState = uiState,
                                        onEvent = onEvent,
                                        sharedTransitionScope = sharedTransitionScope,
                                        animatedVisibilityScope = this@AnimatedContent
                                    )
                                    NowPlayingInfo(
                                        modifier = Modifier.padding(horizontal = LocalDimensions.current.paddingMedium),
                                        title = uiState.currentSong?.basic?.title
                                            ?: stringResource(coreR.string.core_designsystem_unknown_title),
                                        artist = uiState.currentSong?.basic?.artist
                                            ?: stringResource(coreR.string.core_designsystem_unknown_artist),
                                        isFavorite = uiState.currentSong?.isFavorite ?: false,
                                        onFavoriteClick = { onEvent(NowPlayingUiEvent.OnToggleFavorite) }
                                    )
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = LocalDimensions.current.paddingExtraSmall,
                        alignment = Alignment.CenterVertically
                    )
                ) {
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
            }
        }
    }
}