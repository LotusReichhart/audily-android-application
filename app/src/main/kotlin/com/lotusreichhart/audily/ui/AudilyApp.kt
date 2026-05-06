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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import com.lotusreichhart.audily.core.designsystem.component.AudilyBottomSheet
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
import com.lotusreichhart.audily.core.designsystem.adaptive.AudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.adaptive.LocalAudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.adaptive.toAudilyWindowSize
import com.lotusreichhart.audily.core.ui.adaptive.AudilyNavigationSuiteScaffold
import com.lotusreichhart.audily.core.ui.adaptive.AudilyNavItem
import com.lotusreichhart.audily.core.ui.GlobalUiInitializer
import android.app.Activity
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import timber.log.Timber

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AudilyApp(
    modifier: Modifier = Modifier,
    appState: AudilyAppState,
) {
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val audilyWindowSize = remember(windowSizeClass) {
        windowSizeClass.toAudilyWindowSize()
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

    // Giải quyết vấn đề mất MiniPlayer khi xoay màn hình ngoài NAV_BAR_ITEMS
    var isInitialLoadingFinished by rememberSaveable { mutableStateOf(false) }
    
    // Đồng bộ ngược lại appState nếu cần (cho các logic cũ)
    LaunchedEffect(isInitialLoadingFinished) {
        appState.isInitialLoadingFinished = isInitialLoadingFinished
    }

    val isOverlayVisible by remember(
        hasSong,
        shouldExpandPlayer,
        isInitialLoadingFinished
    ) {
        derivedStateOf {
            val isReady = isInitialLoadingFinished || shouldExpandPlayer
            val visible = hasSong && isReady
            visible
        }
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
        windowSize,
        hasSong
    ) {
        appState.updatePlayerAnchors(fullHeight.toFloat(), windowSize)
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
                        isInitialLoadingFinished = true
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

                val navItems = remember {
                    NAV_BAR_ITEMS.map { (key, item) ->
                        AudilyNavItem(
                            selectedIcon = item.selectedIcon,
                            unselectedIcon = item.unselectedIcon,
                            iconTextId = item.iconTextId,
                            key = key
                        )
                    }
                }

                AudilyNavigationSuiteScaffold(
                    currentKey = appState.navigationState.currentTopLevelKey,
                    navItems = navItems,
                    onNavItemClick = { appState.navigator.navigate(it) },
                    navBarVisibilityProgress = navBarVisibilityProgress,
                    expandProgress = progress,
                    bottomBarHeightPx = appState.bottomBarHeightPx,
                    onBottomBarSizeChanged = { appState.bottomBarHeightPx = it },
                    overlayContent = {
                        nowPlayingOverlayContent(Modifier)
                    }
                ) { modifier ->
                    Box(modifier = modifier) {
                        // 1. Lớp nền: Nội dung màn hình
                        val topPadding = if (windowSize == AudilyWindowSize.Compact)
                            LocalDimensions.current.paddingExtraSmall
                        else
                            LocalDimensions.current.paddingSmall

                        navHostContent(topPadding)

                        // Logic 4: BackHandler được khai báo sau NavHost nên sẽ có ưu tiên cao hơn
                        BackHandler(enabled = appState.isExpanded) {
                            appState.collapsePanel()
                        }
                    }
                }

                // SnackbarHost
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(if (windowSize != AudilyWindowSize.Compact) Alignment.BottomEnd else Alignment.BottomCenter)
                        .padding(if (windowSize != AudilyWindowSize.Compact) 16.dp else 0.dp)
                        .padding(bottom = if (windowSize != AudilyWindowSize.Compact) 0.dp else dynamicPadding + 16.dp)
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