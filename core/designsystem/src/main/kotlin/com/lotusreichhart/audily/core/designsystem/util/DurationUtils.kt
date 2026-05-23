package com.lotusreichhart.audily.core.designsystem.util

import java.util.Locale

/**
 * Định dạng thời gian từ milliseconds sang chuỗi mm:ss hoặc hh:mm:ss.
 */
fun Long.formatDuration(): String {
    if (this <= 0) return "00:00"
    
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
