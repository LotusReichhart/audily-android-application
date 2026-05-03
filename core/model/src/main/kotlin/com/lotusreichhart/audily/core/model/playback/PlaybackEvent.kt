package com.lotusreichhart.audily.core.model.playback

import com.lotusreichhart.audily.core.model.song.Song

/**
 * Các sự kiện điều khiển trình phát nhạc (Commands).
 * Đại diện cho ý định của người dùng hoặc hệ thống muốn tác động lên trình phát.
 */
sealed class PlaybackEvent {
    object Resume : PlaybackEvent()
    object Pause : PlaybackEvent()
    object Stop : PlaybackEvent()
    object Next : PlaybackEvent()
    object Previous : PlaybackEvent()
    data class SeekTo(val position: Long) : PlaybackEvent()
    data class SeekBy(val offsetMs: Long) : PlaybackEvent()
    object FastForward : PlaybackEvent()
    object FastRewind : PlaybackEvent()
    data class SetShuffle(val on: Boolean) : PlaybackEvent()
    data class SetRepeatMode(val mode: RepeatMode) : PlaybackEvent()
    data class SetSpeed(val speed: Float) : PlaybackEvent()
    data class SetPitch(val pitch: Float) : PlaybackEvent()

    // Điều khiển Queue chuyên sâu
    data class PlayFromQueue(val songId: Long, val queueIds: List<Long>) : PlaybackEvent()
    data class PlayNext(val song: Song) : PlaybackEvent()
    data class RemoveFromQueue(val songId: Long) : PlaybackEvent()
    data class MoveQueueItem(val from: Int, val to: Int) : PlaybackEvent()
    data class AddSongsToQueue(val songs: List<Song>, val index: Int = -1) : PlaybackEvent()

    // Event nội bộ dùng để truyền dữ liệu Song đã được resolve
    data class SetQueue(val songs: List<Song>, val startIndex: Int, val startPosition: Long = 0) : PlaybackEvent()
    data class SeekToIndex(val index: Int) : PlaybackEvent()
}
