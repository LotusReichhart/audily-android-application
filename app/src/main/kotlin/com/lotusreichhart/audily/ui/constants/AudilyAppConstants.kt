package com.lotusreichhart.audily.ui.constants

object AudilyAppConstants {
    // Giá trị Offset mặc định khi chưa tính toán được anchors (tránh bị NaN)
    const val DEFAULT_PANEL_OFFSET = 2000f
    
    // Các thông số cấu hình Alpha transition (độ trong suốt)
    const val ALPHA_TRANSITION_THRESHOLD = 0.15f
    const val FULL_PLAYER_ALPHA_MULTIPLIER = 2.0f
    const val MINI_PLAYER_ALPHA_THRESHOLD_MULTIPLIER = 6.66f
    
    // Ngưỡng vuốt để xác định chuyển trạng thái (50%)
    const val PANEL_POSITIONAL_THRESHOLD_RATIO = 0.5f
}