package com.lotusreichhart.audily.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Interface điều khiển việc đóng/mở Bottom Sheet toàn cục thông qua Slot API.
 * Giúp các module feature có thể hiển thị UI lên lớp overlay cao nhất mà không bị phụ thuộc vòng.
 */
interface AudilySheetController {
    /**
     * Hiển thị một Bottom Sheet với nội dung tùy chỉnh.
     * @param content Composable hiển thị bên trong sheet.
     * @param isFullScreen Nếu true, sheet sẽ mở rộng toàn màn hình.
     */
    fun showSheet(
        content: @Composable () -> Unit,
        isFullScreen: Boolean = false
    )
    
    /**
     * Đóng Bottom Sheet đang hiển thị.
     */
    fun hideSheet()
}

/**
 * CompositionLocal cung cấp AudilySheetController xuống cây Composable.
 */
val LocalAudilySheetController = staticCompositionLocalOf<AudilySheetController> {
    error("No AudilySheetController provided")
}
