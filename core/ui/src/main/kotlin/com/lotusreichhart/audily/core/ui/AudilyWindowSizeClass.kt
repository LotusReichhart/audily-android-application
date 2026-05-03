package com.lotusreichhart.audily.core.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

/**
 * Lớp đại diện cho kích thước cửa sổ ứng dụng, được đơn giản hóa để dễ sử dụng.
 */
data class AudilyWindowSize(
    val widthSizeClass: WindowWidthSizeClass,
    val isWide: Boolean = widthSizeClass != WindowWidthSizeClass.Compact
)

/**
 * CompositionLocal cung cấp thông tin kích thước màn hình toàn cục.
 * Mặc định là Compact (Điện thoại dọc).
 */
val LocalAudilyWindowSize = staticCompositionLocalOf {
    AudilyWindowSize(widthSizeClass = WindowWidthSizeClass.Compact)
}
