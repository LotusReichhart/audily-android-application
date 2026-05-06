package com.lotusreichhart.audily.feature.nowplaying

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceDark
import com.lotusreichhart.audily.core.ui.GlobalSheetKey
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.adaptive.AudilyAdaptiveLayout
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingMenu
import com.lotusreichhart.audily.feature.nowplaying.component.ExpandedNowPlaying
import com.lotusreichhart.audily.feature.nowplaying.component.CompactNowPlaying
import com.lotusreichhart.audily.feature.nowplaying.component.LandscapeNowPlaying

@Composable
fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    viewModel: NowPlayingViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val globalUiEventBus = LocalGlobalUiEventBus.current

    NowPlayingScreen(
        modifier = modifier,
        uiState = uiState,
        onOpenQueue = {
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    GlobalSheetKey.QUEUE,
                    true
                )
            )
        },
        onCloseClick = onCloseClick,
        onEvent = viewModel::onEvent,
        onTimerClick = {
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    GlobalSheetKey.TIMER,
                    false
                )
            )
        },
        onSpeedPitchClick = {
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    GlobalSheetKey.SPEED_PITCH,
                    false
                )
            )
        },
        onSkipDurationClick = {
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    GlobalSheetKey.SKIP_DURATION,
                    false
                )
            )
        },
        onRingtoneClick = {
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    GlobalSheetKey.RINGTONE,
                    false
                )
            )
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    uiState: NowPlayingUiState,
    onCloseClick: () -> Unit,
    onOpenQueue: () -> Unit,
    onTimerClick: () -> Unit,
    onSpeedPitchClick: () -> Unit,
    onSkipDurationClick: () -> Unit,
    onRingtoneClick: () -> Unit,
    onEvent: (NowPlayingUiEvent) -> Unit,
) {
    var isMenuVisible by remember { mutableStateOf(false) }

    val defaultColor = SurfaceDark
    val adaptiveColor = uiState.paletteColors?.dominant ?: defaultColor
    val vibrantColor = uiState.paletteColors?.vibrant ?: adaptiveColor

    val animatedDominantColor by animateColorAsState(
        targetValue = adaptiveColor,
        animationSpec = tween(1000),
        label = "DominantColorAnimation"
    )
    val animatedVibrantColor by animateColorAsState(
        targetValue = vibrantColor,
        animationSpec = tween(1000),
        label = "VibrantColorAnimation"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundAnimation")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GradientOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .clipToBounds()
            .drawBehind {
                val canvasSize = size
                val baseRadius = canvasSize.maxDimension * 1.2f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(animatedVibrantColor.copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(
                            x = canvasSize.width * (0.1f + 0.3f * gradientOffset),
                            y = canvasSize.height * (0.2f + 0.2f * (1f - gradientOffset))
                        ),
                        radius = baseRadius
                    ),
                    radius = baseRadius,
                    center = Offset(
                        x = canvasSize.width * (0.1f + 0.3f * gradientOffset),
                        y = canvasSize.height * (0.2f + 0.2f * (1f - gradientOffset))
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            animatedDominantColor.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = canvasSize.width * (0.9f - 0.3f * gradientOffset),
                            y = canvasSize.height * (0.8f - 0.2f * (1f - gradientOffset))
                        ),
                        radius = baseRadius * 1.1f
                    ),
                    radius = baseRadius * 1.1f,
                    center = Offset(
                        x = canvasSize.width * (0.9f - 0.3f * gradientOffset),
                        y = canvasSize.height * (0.8f - 0.2f * (1f - gradientOffset))
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            animatedDominantColor.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = canvasSize.width * (0.5f + 0.2f * (gradientOffset * 2f - 1f)),
                            y = canvasSize.height * 0.5f
                        ),
                        radius = baseRadius * 1.4f
                    ),
                    radius = baseRadius * 1.4f,
                    center = Offset(
                        x = canvasSize.width * (0.5f + 0.2f * (gradientOffset * 2f - 1f)),
                        y = canvasSize.height * 0.5f
                    )
                )
            }
    ) {
        SharedTransitionLayout {
            AudilyAdaptiveLayout(
                compact = {
                    CompactNowPlaying(
                        uiState = uiState,
                        onLyricsToggle = { onEvent(NowPlayingUiEvent.OnToggleLyrics) },
                        isMenuVisible = isMenuVisible,
                        onMenuToggle = { isMenuVisible = !isMenuVisible },
                        onCloseClick = onCloseClick,
                        onOpenQueue = onOpenQueue,
                        onEvent = onEvent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                },
                landscape = {
                    LandscapeNowPlaying(
                        uiState = uiState,
                        onLyricsToggle = { onEvent(NowPlayingUiEvent.OnToggleLyrics) },
                        isMenuVisible = isMenuVisible,
                        onMenuToggle = { isMenuVisible = !isMenuVisible },
                        onCloseClick = onCloseClick,
                        onOpenQueue = onOpenQueue,
                        onEvent = onEvent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                },
                expanded = {
                    // Logic: Tablet/Fold sẽ dùng Landscape cho đến khi có Two-Pane đặc thù
                    ExpandedNowPlaying(
                        uiState = uiState,
                        onLyricsToggle = { onEvent(NowPlayingUiEvent.OnToggleLyrics) },
                        isMenuVisible = isMenuVisible,
                        onMenuToggle = { isMenuVisible = !isMenuVisible },
                        onCloseClick = onCloseClick,
                        onOpenQueue = onOpenQueue,
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
            onTimerClick = onTimerClick,
            onSpeedPitchClick = onSpeedPitchClick,
            onSkipDurationClick = onSkipDurationClick,
            onRingtoneClick = onRingtoneClick,
        )
    }
}