package com.lotusreichhart.audily.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationBar
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationBarItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationRail
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationRailItem
import com.lotusreichhart.audily.navigation.TOP_LEVEL_NAV_ITEMS
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import kotlin.math.roundToInt

@Composable
fun AudilyApp(
    appState: AudilyAppState,
    modifier: Modifier = Modifier,
    miniPlayer: @Composable (alpha: Float) -> Unit,
    fullPlayer: @Composable (alpha: Float) -> Unit,
    content: @Composable () -> Unit
) {
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    // Khởi tạo Snackbar để hiển thị thông báo
    val snackbarHostState = remember { SnackbarHostState() }

    // Side-effect: Bắn thông báo khi mất mạng
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = "Bạn đang ở chế độ Offline. Chuyển sang nhạc Local.",
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    // Tính toán thông tin màn hình (Adaptive Info)
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Truyền tất cả dữ liệu xuống hàm UI nội bộ
    AudilyApp(
        appState = appState,
        isLandscape = isLandscape,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        miniPlayer = miniPlayer,
        fullPlayer = fullPlayer,
        content = content
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
    content: @Composable () -> Unit
) {
    var fullHeight by remember { mutableIntStateOf(0) }
    var miniPlayerHeight by remember { mutableIntStateOf(0) }
    var bottomBarHeight by remember { mutableIntStateOf(0) }
    
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior<AudilyPanelState>(
        state = appState.draggableState,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
    
    // Cập nhật mốc Anchors (Giới hạn vuốt) khi màn hình thay đổi kích thước
    LaunchedEffect(fullHeight, miniPlayerHeight, bottomBarHeight, isLandscape) {
        if (fullHeight > 0) {
            val expandedY = 0f
            val collapsedY = if (isLandscape) {
                (fullHeight - miniPlayerHeight).toFloat().coerceAtLeast(0f)
            } else {
                (fullHeight - bottomBarHeight - miniPlayerHeight).toFloat().coerceAtLeast(0f)
            }
            
            val anchors = DraggableAnchors {
                AudilyPanelState.COLLAPSED at collapsedY
                AudilyPanelState.EXPANDED at expandedY
            }
            appState.draggableState.updateAnchors(anchors)
        }
    }

    val progress = appState.expandProgress

    // Xử lý nút Back của hệ thống
    BackHandler(enabled = appState.isExpanded) {
        appState.collapsePanel()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLandscape) {
        // ==========================================
        // MÀN HÌNH NGANG / TABLET
        // ==========================================
        Box(modifier = modifier
            .fillMaxSize()
            .onSizeChanged { fullHeight = it.height }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // LỚP 1: Navigation Rail (Luôn hiển thị bên trái)
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

                // Vùng nội dung chính
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Nội dung gốc (NavHost) - Thêm padding để tránh tai thỏ
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
                    ) {
                        content()
                    }

                    // Panel mở rộng (Mini / Full Player) nhận sự kiện vuốt
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
                        // Wrap MiniPlayer để đo chiều cao tự động
                        Box(modifier = Modifier.onSizeChanged { miniPlayerHeight = it.height }) {
                            miniPlayer(appState.miniPlayerAlpha)
                        }

                        Box {
                            fullPlayer(appState.fullPlayerAlpha)
                        }
                    }
                }
            }

            // Snackbar nổi lên trên cùng
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    } else {
        // ==========================================
        // MÀN HÌNH DỌC (Z-Axis Layering)
        // ==========================================
        Box(modifier = modifier
            .fillMaxSize()
            .onSizeChanged { fullHeight = it.height }
        ) {

            // LỚP 1: NỘI DUNG GỐC (NavHost)
            Box(modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
            ) {
                content()
            }

            // LỚP 2: PANEL MỞ RỘNG (Player)
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
                // Đo chiều cao MiniPlayer
                Box(modifier = Modifier.onSizeChanged { miniPlayerHeight = it.height }) {
                    miniPlayer(appState.miniPlayerAlpha)
                }

                // Full player
                Box {
                    fullPlayer(appState.fullPlayerAlpha)
                }
            }

            // LỚP 3: LỚP ĐIỀU HƯỚNG (Bottom Bar)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset {
                        val yOffset = (progress * appState.bottomBarHeightPx).roundToInt()
                        IntOffset(x = 0, y = yOffset)
                    }
                    .alpha(1f - progress)
                    .onSizeChanged { 
                        bottomBarHeight = it.height 
                        appState.bottomBarHeightPx = it.height.toFloat()
                    }
            ) {
                AudilyNavigationBar(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
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

            // LỚP 4: SNACKBAR (Nằm trên cùng để luôn nhìn thấy)
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    // Đẩy Snackbar lên trên BottomBar
                    .padding(bottom = 80.dp)
            )
        }
    }
}
}