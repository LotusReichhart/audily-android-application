package com.lotusreichhart.audily.core.domain.repository.playback

/**
 * Interface lắng nghe các sự kiện
 * quan trọng từ trình phát nhạc để thực hiện các nhiệm vụ phụ trợ (như lưu trữ session).
 */
interface PlaybackStateListener {
    /**
     * Gọi khi bài hát hiện tại thay đổi hoặc vị trí phát nhạc thay đổi đáng kể.
     */
    suspend fun onPositionDiscontinuity(songId: Long?, position: Long, queueIds: List<Long>)

    /**
     * Gọi khi trạng thái Play/Pause thay đổi.
     */
    suspend fun onPlaybackStateChanged(isPlaying: Boolean, songId: Long?, position: Long, queueIds: List<Long>)

    /**
     * Gọi khi dịch vụ phát nhạc bị hệ thống đóng (onTaskRemoved hoặc onDestroy).
     * Đây là cơ hội cuối cùng để lưu lại phiên phát nhạc.
     */
    suspend fun onSessionEnded(songId: Long?, position: Long, queueIds: List<Long>)
}
