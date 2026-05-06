package com.lotusreichhart.audily.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import com.lotusreichhart.audily.core.designsystem.component.AudilyBottomSheet
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationBar
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationBarItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationRail
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationRailItem
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.LocalDynamicBottomPadding
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceDark
import com.lotusreichhart.audily.core.ui.AudilySheetController
import com.lotusreichhart.audily.core.ui.GlobalSheetRegistry
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.feature.focus.api.navigation.FocusNavKey
import com.lotusreichhart.audily.feature.home.impl.navigation.homeEntry
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingViewModel
import com.lotusreichhart.audily.feature.settings.api.navigation.SettingsNavKey
import com.lotusreichhart.audily.feature.songs.impl.navigation.songsEntry
import com.lotusreichhart.audily.navigation.NAV_BAR_ITEMS
import com.lotusreichhart.audily.core.navigation.toEntries
import com.lotusreichhart.audily.ui.component.AudilyNavHost
import com.lotusreichhart.audily.ui.component.AudilyNowPlayingOverlay
import com.lotusreichhart.audily.ui.constants.AudilyAppConstants
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.lotusreichhart.audily.core.ui.AudilyWindowSize
import com.lotusreichhart.audily.core.ui.LocalAudilyWindowSize
import android.app.Activity
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.lotusreichhart.audily.ui.component.GlobalUiInitializer
import timber.log.Timber
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AudilyApp(
    modifier: Modifier = Modifier,
    appState: AudilyAppState,
) {
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Tính toán WindowSizeClass dựa trên Activity hiện tại
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val audilyWindowSize = remember(windowSizeClass) {
        AudilyWindowSize(widthSizeClass = windowSizeClass.widthSizeClass)
    }

    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = "Bạn đang ở chế độ Offline. Chuyển sang nhạc Local.",
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    AudilyApp(
        appState = appState,
        windowSize = audilyWindowSize,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AudilyApp(
    modifier: Modifier = Modifier,
    appState: AudilyAppState,
    shouldExpandPlayer: Boolean = false,
    windowSize: AudilyWindowSize,
    snackbarHostState: SnackbarHostState,
    nowPlayingViewModel: NowPlayingViewModel = hiltViewModel()
) {
    var fullHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // Quản lý trạng thái của Bottom Sheet toàn cục
    var sheetContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    var isSheetFullScreen by remember { mutableStateOf(false) }

    val sheetController = remember {
        object : AudilySheetController {
            override fun showSheet(content: @Composable () -> Unit, isFullScreen: Boolean) {
                sheetContent = content
                isSheetFullScreen = isFullScreen
            }

            override fun hideSheet() {
                sheetContent = null
            }
        }
    }

    val globalUiEventBus: GlobalUiEventBus = remember { GlobalUiEventBus() }

    // Khởi tạo các thành phần UI toàn cục
    GlobalUiInitializer()

    // Lắng nghe sự kiện từ GlobalUiEventBus
    LaunchedEffect(globalUiEventBus) {
        globalUiEventBus.events.collect { event ->
            when (event) {
                is GlobalUiEvent.OpenSheet -> {
                    val content = GlobalSheetRegistry.getContent(event.key)
                    if (content != null) {
                        sheetController.showSheet(content, event.isFullScreen)
                    }
                }

                GlobalUiEvent.HideSheet -> {
                    sheetController.hideSheet()
                }

                is GlobalUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel
                    )
                }
            }
        }
    }

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = appState.draggableState,
        positionalThreshold = { distance: Float ->
            distance * AudilyAppConstants.PANEL_POSITIONAL_THRESHOLD_RATIO
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    val uiState by nowPlayingViewModel.uiState.collectAsStateWithLifecycle()
    val hasSong = uiState.currentSong != null

    val navBarVisibilityProgress by animateFloatAsState(
        targetValue = if (appState.isBottomBarShown) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "navBarVisibilityProgress"
    )

    // Sử dụng derivedStateOf để đảm bảo Re-composition chính xác khi các điều kiện thay đổi
    val isOverlayVisible by remember(
        hasSong,
        shouldExpandPlayer,
        appState.isInitialLoadingFinished
    ) {
        derivedStateOf {
            val isReady = appState.isInitialLoadingFinished || shouldExpandPlayer
            val visible = hasSong && isReady
            Timber.d("Giải quyết vấn đề Drop khung hình giữa Songs và Nowplaying - AudilyApp theo dõi trạng thái của isOverlayVisible: $visible (hasSong: $hasSong, isReady: $isReady)")
            visible
        }
    }

    LaunchedEffect(isOverlayVisible) {
        Timber.d("Giải quyết vấn đề Drop khung hình giữa Songs và Nowplaying - AudilyApp theo dõi trạng thái của isOverlayVisible: $isOverlayVisible")

    }

    LaunchedEffect(hasSong) {
        if (!hasSong) {
            appState.isPanelVisible = false
            appState.panelHeightPx = 0f
            // Ép buộc đưa trạng thái draggable về COLLAPSED để hiển thị lại BottomBar
            appState.draggableState.snapTo(AudilyPanelState.COLLAPSED)
        }
    }

    LaunchedEffect(appState.navigationState.currentKey) {
        // Nếu màn hình hiện tại không nằm trong danh sách hiển thị của Bottom Bar -> Ẩn nó đi
        val shouldShow = appState.navigationState.currentKey in NAV_BAR_ITEMS.keys
        appState.isBottomBarShown = shouldShow
    }

    LaunchedEffect(
        fullHeight,
        appState.bottomBarHeightPx,
        appState.panelHeightPx,
        windowSize.isWide,
        hasSong
    ) {
        appState.updatePlayerAnchors(fullHeight.toFloat(), windowSize.isWide)
    }

    val progress = appState.expandProgress

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val entryProvider = remember(appState.navigator) {
            entryProvider {
                homeEntry(
                    navigator = appState.navigator,
                    onInitialLoadingFinished = {
                        Timber.d("Giải quyết vấn đề Drop khung hình giữa Songs và Nowplaying - AudilyApp Callback result: Bắt đầu set isInitialLoadingFinished = true")
                        appState.isInitialLoadingFinished = true
                    }
                )
                songsEntry(navigator = appState.navigator)
                entry<FocusNavKey> { SamplePlaceholder("Focus Screen") }
                entry<SettingsNavKey> { SamplePlaceholder("Settings Screen") }
            }
        }

        val dynamicPadding = appState.getContentBottomPadding(density)

        CompositionLocalProvider(
            LocalDynamicBottomPadding provides dynamicPadding,
            LocalAudilySheetController provides sheetController,
            LocalGlobalUiEventBus provides globalUiEventBus,
            LocalAudilyWindowSize provides windowSize
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .onSizeChanged { fullHeight = it.height }
            ) {
                val navHostContent = @Composable { topPadding: Dp ->
                    AudilyNavHost(
                        entries = appState.navigationState.toEntries(entryProvider),
                        onBack = { appState.navigator.goBack() },
                        topPadding = topPadding
                    )
                }

                val nowPlayingOverlayContent = @Composable { modifier: Modifier ->
                    AnimatedVisibility(
                        visible = isOverlayVisible,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                        modifier = modifier
                    ) {
                        AudilyNowPlayingOverlay(
                            appState = appState,
                            nowPlayingViewModel = nowPlayingViewModel,
                            flingBehavior = flingBehavior,
                            navBarVisibilityProgress = navBarVisibilityProgress
                        )
                    }
                }

                if (windowSize.isWide) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Rail nằm ở cánh trái, không bị đè bởi gì cả
                        AudilyNavigationRail {
                            NAV_BAR_ITEMS.forEach { (navKey, navItem) ->
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
                            // Nội dung màn hình
                            navHostContent(LocalDimensions.current.paddingSmall)
                        }
                    }
                    // Overlay cho NowPlaying
                    nowPlayingOverlayContent(Modifier)
                } else {
                    // Portrait Mode
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 1. Lớp nền: Nội dung màn hình
                        navHostContent(LocalDimensions.current.paddingExtraSmall)

                        // 2. Lớp giữa: NowPlaying (Nằm trên nội dung nhưng dưới NavigationBar khi collapsed)
                        nowPlayingOverlayContent(Modifier.align(Alignment.BottomCenter))

                        // 3. Lớp trên cùng: NavigationBar (Đảm bảo click luôn ăn)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset {
                                    val combinedVisibility =
                                        navBarVisibilityProgress * (1f - progress)
                                    val yOffset =
                                        ((1f - combinedVisibility) * appState.bottomBarHeightPx).roundToInt()
                                    IntOffset(x = 0, y = yOffset)
                                }
                                .alpha(navBarVisibilityProgress * (1f - progress))
                                .onSizeChanged {
                                    appState.bottomBarHeightPx = it.height.toFloat()
                                    appState.isBottomBarVisible = it.height > 0
                                }
                        ) {
                            AudilyNavigationBar(
                                containerColor = MaterialTheme.colorScheme.background
                            ) {
                                NAV_BAR_ITEMS.forEach { (navKey, navItem) ->
                                    val selected =
                                        appState.navigationState.currentTopLevelKey == navKey
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
                    }
                }

                // SnackbarHost
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(if (windowSize.isWide) Alignment.BottomEnd else Alignment.BottomCenter)
                        .padding(if (windowSize.isWide) 16.dp else 0.dp)
                        .padding(bottom = if (windowSize.isWide) 0.dp else dynamicPadding + 16.dp)
                )
            }

            // Tầng Overlay cao nhất: BottomSheet
            if (sheetContent != null) {
                AudilyBottomSheet(
                    onDismissRequest = { sheetController.hideSheet() },
                    isFullScreen = isSheetFullScreen,
                    containerColor = SurfaceDark
                ) {
                    sheetContent?.invoke()
                }
            }
        }
    }

    BackHandler(enabled = appState.isExpanded) {
        appState.collapsePanel()
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