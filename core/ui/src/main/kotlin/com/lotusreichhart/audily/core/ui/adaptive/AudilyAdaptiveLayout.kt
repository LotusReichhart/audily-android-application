package com.lotusreichhart.audily.core.ui.adaptive

import androidx.compose.runtime.Composable
import com.lotusreichhart.audily.core.designsystem.adaptive.AudilyWindowSize
import com.lotusreichhart.audily.core.designsystem.adaptive.LocalAudilyWindowSize

/**
 * Một component điều phối layout dựa trên kích thước màn hình.
 * Giúp các feature nhàn hạ hơn trong việc quản lý hiển thị đa thiết bị.
 */
@Composable
fun AudilyAdaptiveLayout(
    portrait: @Composable () -> Unit,
    landscape: @Composable () -> Unit,
    medium: @Composable () -> Unit,
    expanded: @Composable () -> Unit
) {
    val windowSize = LocalAudilyWindowSize.current

    when (windowSize) {
        AudilyWindowSize.Portrait -> portrait()
        AudilyWindowSize.Landscape -> landscape()
        AudilyWindowSize.Medium -> medium()
        AudilyWindowSize.Expanded -> expanded()
    }
}
