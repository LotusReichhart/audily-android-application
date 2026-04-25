package com.lotusreichhart.audily.core.model.prefs

import com.lotusreichhart.audily.core.model.playback.RepeatMode

/**
 * Các thiết lập liên quan đến trình phát nhạc (Persistent).
 */
data class PlaybackSettings(
    val jumpInterval: Int = 10_000, // 10s
    val pauseOnUnplug: Boolean = true,
    val playbackSpeed: Float = 1.0f,
    val volumeNormalization: Boolean = false,

    // Cấu hình phát nhạc cần lưu vĩnh viễn (Persistence)
    val lastPlayedSongId: Long? = null,
    val lastPlaybackPosition: Long = 0,
    val lastQueueIds: List<Long> = emptyList(),
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)
