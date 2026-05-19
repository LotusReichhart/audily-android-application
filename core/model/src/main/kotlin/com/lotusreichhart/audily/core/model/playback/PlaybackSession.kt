package com.lotusreichhart.audily.core.model.playback

/**
 * Model đại diện cho phiên phát nhạc được khôi phục.
 */
data class PlaybackSession(
    val currentSongId: Long?,
    val position: Long,
    val duration: Long,
    val queueIds: List<Long>
)
