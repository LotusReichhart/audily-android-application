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
import androidx.compose.material3.Snackbar
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
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import timber.log.Timber

import com.lotusreichhart.audily.core.designsystem.component.AudilyBottomSheet
import com.lotusreichhart.audily.core.designsystem.theme.LocalDynamicBottomPadding
import com.lotusreichhart.audily.core.ui.AudilySheetController
import com.lotusreichhart.audily.core.ui.GlobalSheetRegistry
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.feature.focus.api.navigation.FocusNavKey
import com.lotusreichhart.audily.feature.home.impl.navigation.homeEntry
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingViewModel
import com.lotusreichhart.audily.feature.search.impl.navigation.searchEntry
import com.lotusreichhart.audily.feature.settings.api.navigation.SettingsNavKey
import com.lotusreichhart.audily.feature.songs.impl.navigation.songsEntry
import com.lotusreichhart.audily.navigation.NAV_BAR_ITEMS
import com.lotusreichhart.audily.core.navigation.toEntries
import com.lotusreichhart.audily.core.navigation.LocalNavigator
import com.lotusreichhart.audily.ui.component.AudilyNavHost
import com.lotusreichhart.audily.ui.component.AudilyNowPlayingOverlay
import com.lotusreichhart.audily.ui.constants.AudilyAppConstants
import com.lotusreichhart.audily.core.designsystem.adaptive.AudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.adaptive.LocalAudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.adaptive.rememberAudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.model.AudilySheetState
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.ui.adaptive.AudilyNavigationSuiteScaffold
import com.lotusreichhart.audily.core.ui.adaptive.AudilyNavItem
import com.lotusreichhart.audily.core.ui.util.findActivity
import com.lotusreichhart.audily.feature.edittag.impl.navigation.editTagEntry
import com.lotusreichhart.audily.feature.playlists.impl.navigation.playlistsEntry
import com.lotusreichhart.audily.feature.favorites.impl.navigation.favoritesEntry
import com.lotusreichhart.audily.feature.albums.impl.navigation.albumsEntry

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AudilyApp(
    modifier: Modifier = Modifier,
    appState: AudilyAppState,
    globalUiEventBus: GlobalUiEventBus
) {
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val audilyWindowSize = rememberAudilyWindowSize()

    AudilyApp(
        modifier = modifier,
        appState = appState,
        windowSize = audilyWindowSize,
        snackbarHostState = snackbarHostState,
        globalUiEventBus = globalUiEventBus,
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
    globalUiEventBus: GlobalUiEventBus,
    nowPlayingViewModel: NowPlayingViewModel = hiltViewModel()
) {
    var fullHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // Quản lý trạng thái của Bottom Sheet toàn cục
    var sheetState by remember { mutableStateOf(AudilySheetState()) }

    val sheetController = remember {
        object : AudilySheetController {
            override fun showSheet(
                content: @Composable () -> Unit,
                isFullScreen: Boolean,
                showDragHandle: Boolean,
                enableSwipeToDismiss: Boolean,
                containerColor: Color,
                skipPartiallyExpanded: Boolean
            ) {
                sheetState = AudilySheetState(
                    content = content,
                    isFullScreen = isFullScreen,
                    showDragHandle = showDragHandle,
                    enableSwipeToDismiss = enableSwipeToDismiss,
                    containerColor = containerColor,
                    skipPartiallyExpanded = skipPartiallyExpanded
                )
            }

            override fun hideSheet() {
                sheetState = sheetState.copy(content = null)
            }
        }
    }

    // Lắng nghe sự kiện từ GlobalUiEventBus
    val context = LocalContext.current
    LaunchedEffect(globalUiEventBus) {
        globalUiEventBus.events.collect { event ->
            when (event) {
                is GlobalUiEvent.OpenSheet -> {
                    if (GlobalSheetRegistry.isRegistered(event.key)) {
                        sheetController.showSheet(
                            content = { GlobalSheetRegistry.Render(event.key, event.params) },
                            isFullScreen = event.isFullScreen,
                            showDragHandle = event.isShowDragHandle
                        )
                    }
                }

                GlobalUiEvent.HideSheet -> {
                    sheetController.hideSheet()
                }

                is GlobalUiEvent.ShowSnackbar -> {
                    launch {
                        snackbarHostState.currentSnackbarData?.dismiss()

                        val result = snackbarHostState.showSnackbar(
                            message = event.message.asString(context),
                            actionLabel = event.actionLabel?.asString(context),
                            duration = event.duration
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            event.onAction?.invoke()
                        }
                    }
                }

                GlobalUiEvent.OpenWriteSettingsPermission -> {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                        data = "package:${context.packageName}".toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }

                is GlobalUiEvent.RequestFilePermission -> {
                    val intentSender = event.intentSender as? android.content.IntentSender
                    if (intentSender != null) {
                        try {
                            context.findActivity()?.startIntentSenderForResult(
                                intentSender,
                                1000, // custom request code
                                null,
                                0,
                                0,
                                0
                            )
                        } catch (e: Exception) {
                            Timber.e(e, "AudilyApp - Failed to start IntentSender")
                        }
                    }
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
        if (appState.isExpanded) {
            appState.collapsePanel()
        }
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
                playlistsEntry(navigator = appState.navigator)
                albumsEntry(navigator = appState.navigator)
                favoritesEntry(navigator = appState.navigator)
                searchEntry(navigator = appState.navigator)
                editTagEntry(navigator = appState.navigator)
                entry<FocusNavKey> { SamplePlaceholder("Focus Screen") }
                entry<SettingsNavKey> { SamplePlaceholder("Settings Screen") }
            }
        }

        val dynamicPadding = appState.getContentBottomPadding(density)

        CompositionLocalProvider(
            LocalNavigator provides appState.navigator,
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
                val navHostContent = @Composable {
                    AudilyNavHost(
                        entries = appState.navigationState.toEntries(entryProvider),
                        onBack = { appState.navigator.goBack() },
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
                        navHostContent()

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
                        .align(
                            if (windowSize != AudilyWindowSize.Portrait) Alignment.BottomEnd
                            else Alignment.BottomCenter
                        )
                        .padding(
                            if (windowSize != AudilyWindowSize.Portrait) LocalDimensions.current.paddingMedium
                            else 0.dp
                        )
                        .padding(
                            bottom = if (windowSize != AudilyWindowSize.Portrait || appState.expandProgress > 0.5f) LocalDimensions.current.paddingSmall
                            else dynamicPadding + LocalDimensions.current.paddingSmall
                        ),
                    snackbar = { snackbarData ->
                        Snackbar(
                            snackbarData = snackbarData,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            actionColor = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            // Tầng Overlay cao nhất: BottomSheet
            if (sheetState.content != null) {
                AudilyBottomSheet(
                    onDismissRequest = {
                        sheetController.hideSheet()
                    },
                    isFullScreen = sheetState.isFullScreen,
                    showDragHandle = sheetState.showDragHandle,
                    enableSwipeToDismiss = sheetState.enableSwipeToDismiss,
                    containerColor = sheetState.containerColor,
                    skipPartiallyExpanded = sheetState.skipPartiallyExpanded
                ) {
                    sheetState.content?.invoke()
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