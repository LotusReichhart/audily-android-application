package com.lotusreichhart.audily.core.designsystem.adaptive

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Định nghĩa các loại kích thước màn hình cho Audily.
 */
@Immutable
sealed interface AudilyWindowSize {
    /** Điện thoại dọc (Compact Width) */
    data object Compact : AudilyWindowSize

    /** Điện thoại xoay ngang (Compact Height, Width > Height) */
    data object Landscape : AudilyWindowSize

    /** Tablet hoặc Màn hình gập (Expanded Width) */
    data object Expanded : AudilyWindowSize
}

/**
 * Chuyển đổi từ WindowSizeClass của Material 3 sang AudilyWindowSize.
 */
fun WindowSizeClass.toAudilyWindowSize(): AudilyWindowSize {
    return when {
        widthSizeClass == WindowWidthSizeClass.Expanded -> AudilyWindowSize.Expanded
        heightSizeClass == WindowHeightSizeClass.Compact -> AudilyWindowSize.Landscape
        else -> AudilyWindowSize.Compact
    }
}

/**
 * CompositionLocal cung cấp thông tin kích thước màn hình toàn cục.
 */
val LocalAudilyWindowSize = staticCompositionLocalOf<AudilyWindowSize> {
    AudilyWindowSize.Compact
}
