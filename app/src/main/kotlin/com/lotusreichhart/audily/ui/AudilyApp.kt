package com.lotusreichhart.audily.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationBar
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationBarItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationRail
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationRailItem
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.LocalDynamicBottomPadding
import com.lotusreichhart.audily.core.navigation.toEntries
import com.lotusreichhart.audily.feature.focus.api.navigation.FocusNavKey
import com.lotusreichhart.audily.feature.home.impl.navigation.homeEntry
import com.lotusreichhart.audily.feature.settings.api.navigation.SettingsNavKey
import com.lotusreichhart.audily.feature.songs.impl.navigation.songsEntry
import com.lotusreichhart.audily.navigation.TOP_LEVEL_NAV_ITEMS
import kotlin.math.roundToInt

@Composable
fun AudilyApp(
    appState: AudilyAppState,
    modifier: Modifier = Modifier,
    miniPlayer: @Composable (alpha: Float) -> Unit,
    fullPlayer: @Composable (alpha: Float) -> Unit,
) {
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = "Bạn đang ở chế độ Offline. Chuyển sang nhạc Local.",
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    AudilyApp(
        appState = appState,
        isLandscape = isLandscape,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        miniPlayer = miniPlayer,
        fullPlayer = fullPlayer
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AudilyApp(
    appState: AudilyAppState,
    isLandscape: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    miniPlayer: @Composable (alpha: Float) -> Unit,
    fullPlayer: @Composable (alpha: Float) -> Unit,
) {
    var fullHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior<AudilyPanelState>(
        state = appState.draggableState,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    LaunchedEffect(fullHeight, appState.miniPlayerAlpha, appState.bottomBarHeightPx, isLandscape) {
        if (fullHeight > 0) {
            val expandedY = 0f
            val collapsedY = if (isLandscape) {
                (fullHeight - appState.panelHeightPx).coerceAtLeast(0f)
            } else {
                (fullHeight - appState.bottomBarHeightPx - appState.panelHeightPx).coerceAtLeast(0f)
            }

            val anchors = DraggableAnchors {
                AudilyPanelState.COLLAPSED at collapsedY
                AudilyPanelState.EXPANDED at expandedY
            }
            appState.draggableState.updateAnchors(anchors)
        }
    }

    val progress = appState.expandProgress

    // Animation ẩn/hiện NavigationBar thủ công
    val nvaBarVisibilityProgress by animateFloatAsState(
        targetValue = if (appState.isBottomBarShown) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "NavBarVisibility"
    )

    BackHandler(enabled = appState.isExpanded) {
        appState.collapsePanel()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val entryProvider = entryProvider {
            homeEntry(appState.navigator)
            songsEntry(appState.navigator)
            entry<FocusNavKey> { SamplePlaceholder("Focus Screen") }
            entry<SettingsNavKey> { SamplePlaceholder("Settings Screen") }
        }

        val dynamicPadding = appState.getContentBottomPadding(density)

        CompositionLocalProvider(LocalDynamicBottomPadding provides dynamicPadding) {
            if (isLandscape) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .onSizeChanged { fullHeight = it.height }
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        AudilyNavigationRail {
                            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                                val selected = appState.navigationState.currentTopLevelKey == navKey
                                AudilyNavigationRailItem(
                                    selected = selected,
                                    onClick = { appState.navigator.navigate(navKey) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = navItem.unselectedIcon),
                                            contentDescription = stringResource(id = navItem.iconTextId),
                                            modifier = Modifier.size(LocalDimensions.current.iconSizeLarge)
                                        )
                                    },
                                    selectedIcon = {
                                        Icon(
                                            painter = painterResource(id = navItem.selectedIcon),
                                            contentDescription = stringResource(id = navItem.iconTextId),
                                            modifier = Modifier.size(LocalDimensions.current.iconSizeLarge)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(id = navItem.iconTextId),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = LocalDimensions.current.paddingSmall)
                                    .padding(horizontal = LocalDimensions.current.paddingMedium)
                                    .windowInsetsPadding(
                                        WindowInsets.safeDrawing.only(
                                            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                                        )
                                    )
                            ) {
                                NavDisplay(
                                    entries = appState.navigationState.toEntries(entryProvider),
                                    onBack = { appState.navigator.goBack() }
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset {
                                        IntOffset(x = 0, y = appState.currentPanelOffsetY.roundToInt())
                                    }
                                    .anchoredDraggable(
                                        state = appState.draggableState,
                                        orientation = Orientation.Vertical,
                                        flingBehavior = flingBehavior
                                    )
                            ) {
                                Box(modifier = Modifier.onSizeChanged {
                                    appState.panelHeightPx = it.height.toFloat()
                                    appState.isPanelVisible = it.height > 0
                                }) {
                                    miniPlayer(appState.miniPlayerAlpha)
                                }

                                Box {
                                    fullPlayer(appState.fullPlayerAlpha)
                                }
                            }
                        }
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .onSizeChanged { fullHeight = it.height }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = LocalDimensions.current.paddingSmall)
                            .padding(horizontal = LocalDimensions.current.paddingMedium)
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                                )
                            )
                    ) {
                        NavDisplay(
                            entries = appState.navigationState.toEntries(entryProvider),
                            onBack = { appState.navigator.goBack() }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset {
                                IntOffset(x = 0, y = appState.currentPanelOffsetY.roundToInt())
                            }
                            .anchoredDraggable(
                                state = appState.draggableState,
                                orientation = Orientation.Vertical,
                                flingBehavior = flingBehavior
                            )
                    ) {
                        Box(modifier = Modifier.onSizeChanged {
                            appState.panelHeightPx = it.height.toFloat()
                            appState.isPanelVisible = it.height > 0
                        }) {
                            miniPlayer(appState.miniPlayerAlpha)
                        }

                        Box {
                            fullPlayer(appState.fullPlayerAlpha)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset {
                                // Kết hợp cả animation thủ công và animation Player
                                val combinedVisibility = nvaBarVisibilityProgress * (1f - progress)
                                val yOffset = ((1f - combinedVisibility) * appState.bottomBarHeightPx).roundToInt()
                                IntOffset(x = 0, y = yOffset)
                            }
                            .alpha(nvaBarVisibilityProgress * (1f - progress))
                            .onSizeChanged {
                                appState.bottomBarHeightPx = it.height.toFloat()
                                appState.isBottomBarVisible = it.height > 0
                            }
                    ) {
                        AudilyNavigationBar(
                            containerColor = MaterialTheme.colorScheme.background
                        ) {
                            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                                val selected = appState.navigationState.currentTopLevelKey == navKey
                                AudilyNavigationBarItem(
                                    selected = selected,
                                    onClick = { appState.navigator.navigate(navKey) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = navItem.unselectedIcon),
                                            contentDescription = stringResource(id = navItem.iconTextId),
                                            modifier = Modifier.size(LocalDimensions.current.iconSizeLarge)
                                        )
                                    },
                                    selectedIcon = {
                                        Icon(
                                            painter = painterResource(id = navItem.selectedIcon),
                                            contentDescription = stringResource(id = navItem.iconTextId),
                                            modifier = Modifier.size(LocalDimensions.current.iconSizeLarge)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(id = navItem.iconTextId),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = dynamicPadding + 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SamplePlaceholder(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$name Placeholder", style = MaterialTheme.typography.headlineMedium)
    }
}