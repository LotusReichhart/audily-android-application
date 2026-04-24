package com.lotusreichhart.audily.core.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    // Padding & Margin chung
    val paddingExtraSmall: Dp = 4.dp,
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,
    val paddingLarge: Dp = 24.dp,
    val paddingExtraLarge: Dp = 32.dp,

    // Kích thước Icon
    val iconSizeSmall: Dp = 16.dp,
    val iconSizeMedium: Dp = 24.dp, // Chuẩn Material 3
    val iconSizeLarge: Dp = 32.dp,

    // Kích thước phần tử UI
    val buttonHeight: Dp = 48.dp,   // Chuẩn Accessibility
    val bottomBarHeight: Dp = 60.dp,
    val miniPlayerHeight: Dp = 64.dp,

    val iconButtonHeight: Dp = 40.dp,

    // Bo góc (Corner Radius) - Dùng nếu bạn không muốn dùng MaterialTheme.shapes
    val cornerRadiusExtraSmall: Dp = 4.dp,
    val cornerRadiusSmall: Dp = 8.dp,
    val cornerRadiusMedium: Dp = 16.dp,
    val cornerRadiusLarge: Dp = 24.dp
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }
val LocalDynamicBottomPadding = compositionLocalOf { 0.dp }