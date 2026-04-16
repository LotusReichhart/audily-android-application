package com.lotusreichhart.audily.core.model.prefs

/**
 * Các thiết lập liên quan đến trình phát nhạc (Persistent).
 */
data class PlayerSettings(
    val jumpInterval: Int = 10_000, // 10s
    val pauseOnUnplug: Boolean = true,
    val playbackSpeed: Float = 1.0f,
    val volumeNormalization: Boolean = false
)
