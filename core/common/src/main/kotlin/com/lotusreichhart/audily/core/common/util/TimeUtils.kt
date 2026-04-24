package com.lotusreichhart.audily.core.common.util

import java.util.Locale

/**
 * Các hàm tiện ích xử lý thời gian.
 */
object TimeUtils {
    /**
     * Chuyển đổi mili giây sang định dạng HH:mm:ss hoặc mm:ss.
     */
    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }
}
