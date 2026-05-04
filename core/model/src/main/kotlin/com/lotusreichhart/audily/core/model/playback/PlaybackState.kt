package com.lotusreichhart.audily.core.model.playback
/**
 * Tình trạng phát nhạc hiện tại (Runtime snapshot).
 * Phản ánh chính xác những gì đang diễn ra trong Player.
 */
data class PlaybackState(
    val nowPlayingState: NowPlayingState,
    val currentSongId: Long?,
    val queueIds: List<Long> = emptyList(),
    val isShuffleOn: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val duration: Long = 0,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f
) {
    companion object {
        val INITIAL = PlaybackState(
            nowPlayingState = NowPlayingState.IDLE,
            currentSongId = null,
            queueIds = emptyList(),
            isShuffleOn = false,
            repeatMode = RepeatMode.OFF,
            duration = 0,
            speed = 1.0f,
            pitch = 1.0f
        )
    }
}
