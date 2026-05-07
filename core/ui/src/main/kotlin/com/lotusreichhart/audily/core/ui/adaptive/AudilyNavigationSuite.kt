package com.lotusreichhart.audily.core.ui.adaptive

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.designsystem.adaptive.AudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.adaptive.LocalAudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationBar
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationBarItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationRail
import com.lotusreichhart.audily.core.designsystem.component.AudilyNavigationRailItem
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import androidx.compose.runtime.movableContentOf
import kotlin.math.roundToInt

/**
 * Một mục điều hướng chuẩn cho Audily.
 */
data class AudilyNavItem(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextId: Int,
    val key: NavKey
)

/**
 * Bộ khung điều hướng Adaptive cho Audily.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AudilyNavigationSuiteScaffold(
    currentKey: NavKey?,
    navItems: List<AudilyNavItem>,
    onNavItemClick: (key: NavKey) -> Unit,
    navBarVisibilityProgress: Float = 1f,
    expandProgress: Float = 0f,
    bottomBarHeightPx: Float = 0f,
    onBottomBarSizeChanged: (Float) -> Unit = {},
    overlayContent: @Composable BoxScope.() -> Unit = {},
    content: @Composable (modifier: Modifier) -> Unit
) {
    val windowSize = LocalAudilyWindowSize.current

    val movableMainContent = remember(content) {
        movableContentOf {
            content(Modifier.fillMaxSize())
        }
    }

    val movableOverlayContent = remember(overlayContent) {
        movableContentOf {
            Box(modifier = Modifier.fillMaxSize()) {
                overlayContent()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (windowSize) {
            AudilyWindowSize.Compact -> {
                val combinedVisibility = navBarVisibilityProgress * (1f - expandProgress)
                Scaffold(
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .offset {
                                    val yOffset =
                                        ((1f - combinedVisibility) * bottomBarHeightPx).roundToInt()
                                    IntOffset(x = 0, y = yOffset)
                                }
                                .alpha(combinedVisibility)
                                .onSizeChanged { onBottomBarSizeChanged(it.height.toFloat()) }
                        ) {
                            AudilyNavigationBar(
                                containerColor = MaterialTheme.colorScheme.background
                            ) {
                                navItems.forEach { item ->
                                    val selected = currentKey == item.key
                                    AudilyNavigationBarItem(
                                        selected = selected,
                                        onClick = { onNavItemClick(item.key) },
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = item.unselectedIcon),
                                                contentDescription = stringResource(id = item.iconTextId),
                                                modifier = Modifier.size(LocalDimensions.current.iconSizeMedium)
                                            )
                                        },
                                        selectedIcon = {
                                            Icon(
                                                painter = painterResource(id = item.selectedIcon),
                                                contentDescription = stringResource(id = item.iconTextId),
                                                modifier = Modifier.size(LocalDimensions.current.iconSizeMedium)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(id = item.iconTextId),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) {
                    // QUAN TRỌNG: Không dùng padding(innerPadding) ở đây vì NavHost và Overlay tự quản lý padding/offset
                    movableMainContent()
                }
            }

            AudilyWindowSize.Landscape, AudilyWindowSize.Expanded -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    AudilyNavigationRail {
                        navItems.forEach { item ->
                            val selected = currentKey == item.key
                            AudilyNavigationRailItem(
                                selected = selected,
                                onClick = { onNavItemClick(item.key) },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = item.unselectedIcon),
                                        contentDescription = stringResource(id = item.iconTextId),
                                        modifier = Modifier.size(LocalDimensions.current.iconSizeMedium)
                                    )
                                },
                                selectedIcon = {
                                    Icon(
                                        painter = painterResource(id = item.selectedIcon),
                                        contentDescription = stringResource(id = item.iconTextId),
                                        modifier = Modifier.size(LocalDimensions.current.iconSizeMedium)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(id = item.iconTextId),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()) {
                        movableMainContent()
                    }
                }
            }
        }

        // Luôn nằm ngoài cùng để có thể Full Screen 100%
        movableOverlayContent()
    }
}
