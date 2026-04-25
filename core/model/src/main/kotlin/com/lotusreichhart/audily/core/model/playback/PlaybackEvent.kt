package com.lotusreichhart.audily.core.model.playback

/**
 * Các sự kiện điều khiển trình phát nhạc (Commands).
 * Đại diện cho ý định của người dùng hoặc hệ thống muốn tác động lên trình phát.
 */
sealed class PlaybackEvent {
    object Play : PlaybackEvent()
    object Pause : PlaybackEvent()
    object Next : PlaybackEvent()
    object Previous : PlaybackEvent()
    data class SeekTo(val position: Long) : PlaybackEvent()
    data class SetShuffle(val on: Boolean) : PlaybackEvent()
    data class SetRepeatMode(val mode: RepeatMode) : PlaybackEvent()
    data class SetSpeed(val speed: Float) : PlaybackEvent()
    data class SetPitch(val pitch: Float) : PlaybackEvent()

    // Điều khiển Queue chuyên sâu
    data class PlayFromQueue(val songId: Long, val queueIds: List<Long>) : PlaybackEvent()
    data class RemoveFromQueue(val songId: Long) : PlaybackEvent()
    data class MoveQueueItem(val from: Int, val to: Int) : PlaybackEvent()
}
