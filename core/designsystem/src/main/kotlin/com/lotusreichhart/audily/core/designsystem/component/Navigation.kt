package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

/**
 * Thẻ diều hướng cấp cao nhất (Bottom Navigation) được tùy chỉnh cho Audily.
 * Loại bỏ các hiệu ứng focus và khoảng cách mặc định của Material 3.
 */
@Composable
fun AudilyNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = AudilyNavigationDefaults.navigationContentColor(),
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .height(LocalDimensions.current.bottomBarHeight)
                .padding(horizontal = LocalDimensions.current.paddingSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * Item cho thanh điều hướng dưới cùng.
 * - Icon và Title cách nhau 4dp.
 * - Cùng nhận một màu khi được chọn hoặc không.
 * - Không có hiệu ứng focus pill của Material 3.
 */
@Composable
fun RowScope.AudilyNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    val contentColor = if (selected) {
        AudilyNavigationDefaults.navigationSelectedItemColor()
    } else {
        AudilyNavigationDefaults.navigationContentColor()
    }

    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .weight(1f)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box {
                    if (selected) selectedIcon() else icon()
                }
                if (label != null) {
                    label()
                }
            }
        }
    }
}

/**
 * Thanh điều hướng dọc (Navigation Rail) cho màn hình ngang / Tablet.
 */
@Composable
fun AudilyNavigationRail(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    contentColor: Color = AudilyNavigationDefaults.navigationContentColor(),
    header: @Composable (() -> Unit)? = null,
    windowInsets: WindowInsets = NavigationRailDefaults.windowInsets,
    content: @Composable () -> Unit,
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(windowInsets),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingSmall)
        ) {
            if (header != null) {
                header()
                Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))
            }
            content()
        }
    }
}

@Composable
fun AudilyNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    val contentColor = if (selected) {
        AudilyNavigationDefaults.navigationSelectedItemColor()
    } else {
        AudilyNavigationDefaults.navigationContentColor()
    }

    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(
                LocalDimensions.current.paddingSmall
            ),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box {
                    if (selected) selectedIcon() else icon()
                }
                if (label != null) {
                    label()
                }
            }
        }
    }
}

object AudilyNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.primary
}

@Composable
@Preview
fun AudilyNavigationBarItemPreview() {
    MaterialTheme {
        Surface {
            AudilyNavigationBar {
                AudilyNavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                    label = { Text(text = "Home", style = MaterialTheme.typography.labelSmall) }
                )
                AudilyNavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    label = { Text(text = "Search", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}
