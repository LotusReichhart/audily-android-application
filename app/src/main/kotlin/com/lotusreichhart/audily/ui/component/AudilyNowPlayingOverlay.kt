package com.lotusreichhart.audily.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import com.lotusreichhart.audily.feature.nowplaying.MiniNowPlaying
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingScreen
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingViewModel
import com.lotusreichhart.audily.ui.AudilyAppState
import kotlin.math.roundToInt
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.ui.layout.layout

import com.lotusreichhart.audily.core.designsystem.adaptive.LocalAudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.adaptive.AudilyWindowSize

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudilyNowPlayingOverlay(
    appState: AudilyAppState,
    nowPlayingViewModel: NowPlayingViewModel,
    flingBehavior: FlingBehavior,
    navBarVisibilityProgress: Float,
    modifier: Modifier = Modifier
) {
    val windowSize = LocalAudilyWindowSize.current
    val hasBottomBar = windowSize == AudilyWindowSize.Portrait

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (appState.expandProgress > 0f) Modifier.fillMaxSize()
                else Modifier.wrapContentHeight()
            )
            .offset {
                val barOffset = if (hasBottomBar && appState.bottomBarHeightPx > 0) {
                    (1f - navBarVisibilityProgress) * appState.bottomBarHeightPx * (1f - appState.expandProgress)
                } else 0f

                IntOffset(
                    x = 0,
                    y = (appState.currentPanelOffsetY + barOffset).roundToInt()
                )
            }
            .anchoredDraggable(
                state = appState.draggableState,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior
            )
    ) {
        // 1. MiniNowPlaying
        MiniNowPlaying(
            modifier = Modifier
                .graphicsLayer {
                    alpha = appState.miniPlayerAlpha
                },
            onClick = { appState.expandPanel() },
            viewModel = nowPlayingViewModel
        )

        // 2. NowPlayingScreen
        Box(
            modifier = Modifier
                .graphicsLayer {
                    alpha = appState.fullPlayerAlpha
                    // Đẩy đi xa để không bắt touch khi hoàn toàn ẩn
                    translationY = if (appState.fullPlayerAlpha <= 0f) 10000f else 0f
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    // Nếu đang thu nhỏ thì báo cáo cao là 0 để giải phóng vùng BottomBar
                    val reportedHeight = if (appState.expandProgress > 0f) placeable.height else 0
                    layout(placeable.width, reportedHeight) {
                        placeable.placeRelative(0, 0)
                    }
                }
        ) {
            NowPlayingScreen(
                onCloseClick = { appState.collapsePanel() },
                viewModel = nowPlayingViewModel
            )
        }
    }
}
