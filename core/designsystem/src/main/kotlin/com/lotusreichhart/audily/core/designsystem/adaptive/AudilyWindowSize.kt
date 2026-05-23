package com.lotusreichhart.audily.core.designsystem.adaptive

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Định nghĩa các loại kích thước màn hình cho Audily.
 */
@Immutable
sealed interface AudilyWindowSize {
    data object Portrait : AudilyWindowSize          // Dọc hẹp
    data object Landscape : AudilyWindowSize         // Ngang hẹp
    data object Medium : AudilyWindowSize            // Vuông dọc (2:3)
    data object Expanded : AudilyWindowSize          // Vuông ngang (3:2)
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun rememberAudilyWindowSize(): AudilyWindowSize {
    val configuration = LocalConfiguration.current

    val width = configuration.screenWidthDp.toFloat()
    val height = configuration.screenHeightDp.toFloat()

    return remember(width, height) {
        // Kiểm tra xem màn hình đang nằm ngang hay nằm dọc
        val isLandscapeOrientation = width > height

        if (isLandscapeOrientation) {
            // ==========================================
            // NHÓM MÀN HÌNH NGANG (Width > Height)
            // ==========================================
            if (height < 480f) {
                // Chiều cao rất hẹp (< 480dp) -> Điện thoại xoay ngang
                AudilyWindowSize.Landscape
            } else {
                // Chiều cao đủ lớn -> Tablet xoay ngang hoặc Fold khi đang cầm dọc
                AudilyWindowSize.Expanded
            }
        } else {
            // ==========================================
            // NHÓM MÀN HÌNH DỌC (Height >= Width)
            // ==========================================
            if (width < 600f) {
                // Chiều rộng nhỏ (< 600dp) -> Điện thoại dọc
                AudilyWindowSize.Portrait
            } else {
                // Chiều rộng lớn (>= 600dp) -> Tablet dọc hoặc Fold mở khi đang cầm ngang
                AudilyWindowSize.Medium
            }
        }
    }
}

val LocalAudilyWindowSize = staticCompositionLocalOf<AudilyWindowSize> {
    AudilyWindowSize.Portrait
}
