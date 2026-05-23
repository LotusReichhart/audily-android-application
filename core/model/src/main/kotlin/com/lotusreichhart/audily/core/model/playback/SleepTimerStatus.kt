package com.lotusreichhart.audily.core.model.playback

/**
 * Trạng thái của bộ hẹn giờ tắt nhạc.
 * @param remainingTimeMs Thời gian còn lại tính bằng mili giây.
 * @param isActive Bộ hẹn giờ có đang chạy hay không.
 */
data class SleepTimerStatus(
    val remainingTimeMs: Long = 0L,
    val isActive: Boolean = false
) {
    companion object {
        val INITIAL = SleepTimerStatus()
    }
}
