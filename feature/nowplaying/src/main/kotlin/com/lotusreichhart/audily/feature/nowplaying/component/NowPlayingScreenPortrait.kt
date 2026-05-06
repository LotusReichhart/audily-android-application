package com.lotusreichhart.audily.feature.nowplaying.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceVariantDark
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiEvent
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiState
import com.lotusreichhart.audily.feature.nowplaying.R
import com.lotusreichhart.audily.feature.nowplaying.constants.SharedContentStateConstants
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
internal fun NowPlayingScreenPortrait(
    modifier: Modifier = Modifier,
    uiState: NowPlayingUiState,
    onLyricsToggle: () -> Unit,
    isMenuVisible: Boolean,
    onMenuToggle: () -> Unit,
    onCloseClick: () -> Unit,
    onOpenQueue: () -> Unit,
    onEvent: (NowPlayingUiEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val paddingMedium = LocalDimensions.current.paddingMedium
    val largeArtworkSize = screenWidth - (paddingMedium * 2)

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
        bottomBar = {
            NowPlayingExtension(
                modifier = Modifier
                    .padding(horizontal = LocalDimensions.current.paddingMedium)
                    .padding(bottom = LocalDimensions.current.paddingMedium),
                isLyricsVisible = uiState.isLyricsVisible,
                isMenuVisible = isMenuVisible,
                onQueueClick = onOpenQueue,
                onLyricsClick = onLyricsToggle,
                onExtendClick = onMenuToggle
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(LocalDimensions.current.paddingMedium)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceVariantDark.copy(alpha = 0.7f))
                                        .padding(LocalDimensions.current.paddingSmall),
                                    verticalAlignment = Alignment.CenterVertically
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
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))
                                    NowPlayingInfo(
                                        title = uiState.currentSong?.basic?.title
                                            ?: stringResource(coreR.string.core_designsystem_unknown_title),
                                        artist = uiState.currentSong?.basic?.artist
                                            ?: stringResource(coreR.string.core_designsystem_unknown_artist),
                                        isFavorite = uiState.currentSong?.isFavorite ?: false,
                                        onFavoriteClick = { onEvent(NowPlayingUiEvent.OnToggleFavorite) }
                                    )
                                }
                                NowPlayingNoLyrics(modifier = Modifier.weight(1f))
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                NowPlayingArtworkPager(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    artworkModifier = Modifier.size(largeArtworkSize),
                                    uiState = uiState,
                                    onEvent = onEvent,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))
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

            Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))

            Column(
                modifier = Modifier.fillMaxWidth()
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

                Spacer(modifier = Modifier.height(LocalDimensions.current.paddingLarge))

                NowPlayingControls(
                    modifier = Modifier
                        .padding(horizontal = LocalDimensions.current.paddingMedium)
                        .padding(bottom = LocalDimensions.current.paddingMedium),
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
            Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))
        }
    }
}