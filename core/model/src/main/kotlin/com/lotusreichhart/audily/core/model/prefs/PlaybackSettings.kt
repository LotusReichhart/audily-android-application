package com.lotusreichhart.audily.core.model.prefs

import com.lotusreichhart.audily.core.model.playback.RepeatMode

/**
 * Các thiết lập liên quan đến trình phát nhạc (Persistent).
 */
data class PlaybackSettings(
    val skipDuration: Int = 10_000, // 10s
    val pauseOnUnplug: Boolean = true,
    val playbackSpeed: Float = 1.0f,
    val playbackPitch: Float = 1.0f,
    val volumeNormalization: Boolean = false,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)
