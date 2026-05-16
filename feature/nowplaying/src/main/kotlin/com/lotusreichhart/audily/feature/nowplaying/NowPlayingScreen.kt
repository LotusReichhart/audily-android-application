package com.lotusreichhart.audily.feature.nowplaying

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.ui.GlobalMenuCaller
import com.lotusreichhart.audily.core.ui.GlobalParams
import com.lotusreichhart.audily.core.ui.GlobalSheetKey
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.core.ui.adaptive.AudilyAdaptiveLayout
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.StatusBarColorEffect
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingMenu
import com.lotusreichhart.audily.feature.nowplaying.layout.ExpandedNowPlayingLayout
import com.lotusreichhart.audily.feature.nowplaying.layout.PortraitNowPlayingLayout
import com.lotusreichhart.audily.feature.nowplaying.layout.LandscapeNowPlayingLayout
import com.lotusreichhart.audily.feature.nowplaying.component.PlaybackParametersSheet
import com.lotusreichhart.audily.feature.nowplaying.component.PlaybackSkipDurationSheet
import com.lotusreichhart.audily.feature.nowplaying.component.PlaybackTimerSheet
import com.lotusreichhart.audily.feature.nowplaying.component.SleepTimerCountdownDialog
import com.lotusreichhart.audily.feature.nowplaying.layout.MediumNowPlayingLayout
import com.lotusreichhart.audily.feature.nowplaying.queue.QueueScreen
import com.lotusreichhart.audily.feature.nowplaying.util.nowPlayingBackground

@Composable
fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    viewModel: NowPlayingViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val globalUiEventBus = LocalGlobalUiEventBus.current
    val sheetController = LocalAudilySheetController.current

    val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant
    var showTimerCountdown by remember { mutableStateOf(false) }

    StatusBarColorEffect(isLightIcon = true)

    NowPlayingScreen(
        modifier = modifier,
        uiState = uiState,
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
        onCloseClick = onCloseClick,
        onMenuClick = {
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    key = GlobalSheetKey.SONG_MENU,
                    params = mapOf(
                        GlobalParams.PARAM_SONG to uiState.currentSong,
                        GlobalParams.PARAM_CALLER to GlobalMenuCaller.NOW_PLAYING
                    ),
                    isShowDragHandle = false
                )
            )
        },
        onEvent = viewModel::onEvent,
        onTimerClick = {
            sheetController.showSheet(
                content = {
                    PlaybackTimerSheet(
                        initialMinutes = 0,
                        onSave = { minutes ->
                            viewModel.onEvent(NowPlayingUiEvent.OnSetSleepTimer(minutes))
                            sheetController.hideSheet()
                        }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        },
        onTimerActiveClick = { showTimerCountdown = true },
        onSpeedPitchClick = {
            sheetController.showSheet(
                content = {
                    PlaybackParametersSheet(
                        initialSpeed = uiState.playbackState.speed,
                        initialPitch = uiState.playbackState.pitch,
                        onSave = { speed, pitch ->
                            viewModel.onEvent(NowPlayingUiEvent.OnSetSpeedAndPitch(speed, pitch))
                            viewModel.onEvent(
                                NowPlayingUiEvent.OnSavePlaybackParameters(
                                    speed,
                                    pitch
                                )
                            )
                            sheetController.hideSheet()
                        }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        },
        onSkipDurationClick = {
            sheetController.showSheet(
                content = {
                    PlaybackSkipDurationSheet(
                        initialSeconds = uiState.skipDuration,
                        onSave = { duration ->
                            viewModel.onEvent(NowPlayingUiEvent.OnSaveSkipDuration(duration))
                            sheetController.hideSheet()
                        }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        }
    )

    if (showTimerCountdown) {
        SleepTimerCountdownDialog(
            remainingTimeMs = uiState.sleepTimerStatus.remainingTimeMs,
            onDismiss = { showTimerCountdown = false },
            onStopTimer = { viewModel.onEvent(NowPlayingUiEvent.OnSetSleepTimer(null)) }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    uiState: NowPlayingUiState,
    onCloseClick: () -> Unit,
    onMenuClick: () -> Unit,
    onOpenQueue: () -> Unit,
    onTimerClick: () -> Unit,
    onTimerActiveClick: () -> Unit,
    onSpeedPitchClick: () -> Unit,
    onSkipDurationClick: () -> Unit,
    onEvent: (NowPlayingUiEvent) -> Unit,
) {
    var isMenuVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nowPlayingBackground(uiState.paletteColors)
    ) {
        SharedTransitionLayout {
            AudilyAdaptiveLayout(
                portrait = {
                    PortraitNowPlayingLayout(
                        uiState = uiState,
                        onMenuClick = onMenuClick,
                        onLyricsToggle = { onEvent(NowPlayingUiEvent.OnToggleLyrics) },
                        onExtendClick = { isMenuVisible = !isMenuVisible },
                        onTimerActiveClick = onTimerActiveClick,
                        onCloseClick = onCloseClick,
                        onOpenQueue = onOpenQueue,
                        onEvent = onEvent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                },
                landscape = {
                    LandscapeNowPlayingLayout(
                        uiState = uiState,
                        onMenuClick = onMenuClick,
                        onLyricsToggle = { onEvent(NowPlayingUiEvent.OnToggleLyrics) },
                        onExtendClick = { isMenuVisible = !isMenuVisible },
                        onTimerActiveClick = onTimerActiveClick,
                        onCloseClick = onCloseClick,
                        onOpenQueue = onOpenQueue,
                        onEvent = onEvent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                },
                medium = {
                    MediumNowPlayingLayout(
                        uiState = uiState,
                        onMenuClick = onMenuClick,
                        onLyricsToggle = { onEvent(NowPlayingUiEvent.OnToggleLyrics) },
                        onExtendClick = { isMenuVisible = !isMenuVisible },
                        onTimerActiveClick = onTimerActiveClick,
                        onCloseClick = onCloseClick,
                        onEvent = onEvent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                },
                expanded = {
                    ExpandedNowPlayingLayout(
                        uiState = uiState,
                        onMenuClick = onMenuClick,
                        onLyricsToggle = { onEvent(NowPlayingUiEvent.OnToggleLyrics) },
                        onExtendClick = { isMenuVisible = !isMenuVisible },
                        onTimerActiveClick = onTimerActiveClick,
                        onCloseClick = onCloseClick,
                        onEvent = onEvent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
            )
        }

        // Lớp nền mờ khi menu mở
        AnimatedVisibility(
            visible = isMenuVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isMenuVisible = false }
            )
        }

        NowPlayingMenu(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = LocalDimensions.current.paddingMedium,
                    bottom = 48.dp
                ),
            isVisible = isMenuVisible,
            onTimerClick = {
                onTimerClick()
                isMenuVisible = false
            },
            onSpeedPitchClick = {
                onSpeedPitchClick()
                isMenuVisible = false
            },
            onSkipDurationClick = {
                onSkipDurationClick()
                isMenuVisible = false
            }
        )
    }
}