package com.lotusreichhart.audily.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudilyNowPlayingOverlay(
    appState: AudilyAppState,
    nowPlayingViewModel: NowPlayingViewModel,
    flingBehavior: FlingBehavior,
    modifier: Modifier = Modifier
) {
    // Root Box không dùng fillMaxSize() để tránh chặn touch toàn màn hình
    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = appState.currentPanelOffsetY.roundToInt()
                )
            }
            .anchoredDraggable(
                state = appState.draggableState,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior
            )
    ) {
        // Nội dung bên trong (MiniPlayer và FullPlayer)
        Box(modifier = Modifier.fillMaxSize()) {
            // MiniNowPlaying
            MiniNowPlaying(
                modifier = Modifier
                    .onSizeChanged {
                        appState.panelHeightPx = it.height.toFloat()
                        appState.isPanelVisible = it.height > 0
                    }
                    .graphicsLayer {
                        alpha = appState.miniPlayerAlpha
                    },
                onClick = { appState.expandPanel() },
                viewModel = nowPlayingViewModel
            )

            // NowPlayingScreen
            NowPlayingScreen(
                modifier = Modifier.graphicsLayer {
                    alpha = appState.fullPlayerAlpha
                    // Đẩy hẳn màn hình đi khi không hiển thị để thoát khỏi vùng touch
                    translationY = if (appState.fullPlayerAlpha <= 0f) 10000f else 0f
                },
                onCloseClick = { appState.collapsePanel() },
                viewModel = nowPlayingViewModel
            )
        }
    }
}
