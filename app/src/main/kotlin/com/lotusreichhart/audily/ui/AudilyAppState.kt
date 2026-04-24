package com.lotusreichhart.audily.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.lotusreichhart.audily.core.domain.util.NetworkMonitor
import com.lotusreichhart.audily.core.navigation.NavigationState
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.core.navigation.rememberNavigationState
import com.lotusreichhart.audily.feature.home.api.navigation.HomeNavKey
import com.lotusreichhart.audily.navigation.TOP_LEVEL_NAV_ITEMS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AudilyPanelState {
    COLLAPSED, EXPANDED
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberAudilyAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AudilyAppState {
    val draggableState = remember {
        AnchoredDraggableState<AudilyPanelState>(
            initialValue = AudilyPanelState.COLLAPSED
        )
    }

    val navigationState = rememberNavigationState(
        startKey = HomeNavKey,
        topLevelKeys = TOP_LEVEL_NAV_ITEMS.keys
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }

    return remember(
        coroutineScope,
        networkMonitor,
        draggableState,
        navigationState,
        navigator
    ) {
        AudilyAppState(
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            draggableState = draggableState,
            navigationState = navigationState,
            navigator = navigator
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
class AudilyAppState(
    val coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    val draggableState: AnchoredDraggableState<AudilyPanelState>,
    val navigationState: NavigationState,
    val navigator: Navigator
) {
    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isExpanded: Boolean
        get() = draggableState.currentValue == AudilyPanelState.EXPANDED

    val expandProgress: Float
        get() {
            val collapsedOffset = draggableState.anchors.positionOf(AudilyPanelState.COLLAPSED).takeIf { !it.isNaN() } ?: 2000f
            val expandedOffset = draggableState.anchors.positionOf(AudilyPanelState.EXPANDED).takeIf { !it.isNaN() } ?: 0f
            val currentOffset = draggableState.offset.takeIf { !it.isNaN() } ?: collapsedOffset

            if (collapsedOffset == expandedOffset) return 0f
            return 1f - ((currentOffset - expandedOffset) / (collapsedOffset - expandedOffset)).coerceIn(0f, 1f)
        }

    val currentPanelOffsetY: Float
        get() = draggableState.offset.takeIf { !it.isNaN() } ?: 2000f

    var bottomBarHeightPx by mutableFloatStateOf(0f)
        internal set

    var panelHeightPx by mutableFloatStateOf(0f)
        internal set

    var isBottomBarShown by mutableStateOf(true)

    var isBottomBarVisible by mutableStateOf(true)

    var isPanelVisible by mutableStateOf(false)

    /**
     * Tính toán padding dưới cùng cho nội dung (NavDisplay).
     * Bao gồm: Chiều cao BottomBar (nếu hiện) + Chiều cao MiniPlayer (nếu hiện).
     */
    fun getContentBottomPadding(density: Density): Dp {
        var totalHeightPx = 0f
        if (isBottomBarShown && isBottomBarVisible) {
            totalHeightPx += bottomBarHeightPx
        }
        if (isPanelVisible) {
            totalHeightPx += panelHeightPx
        }
        
        return with(density) { totalHeightPx.toDp() }
    }

    // Công thức tính Alpha (Độ trong suốt)
    val miniPlayerAlpha: Float
        get() = (1f - (expandProgress * 6.66f)).coerceIn(0f, 1f)

    val fullPlayerAlpha: Float
        get() = ((expandProgress - 0.15f) * 2.0f).coerceIn(0f, 1f)

    fun collapsePanel() {
        coroutineScope.launch {
            draggableState.animateTo(AudilyPanelState.COLLAPSED)
        }
    }

    fun expandPanel() {
        coroutineScope.launch {
            draggableState.animateTo(AudilyPanelState.EXPANDED)
        }
    }
}