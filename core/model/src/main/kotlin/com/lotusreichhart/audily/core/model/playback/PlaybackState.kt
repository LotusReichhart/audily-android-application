package com.lotusreichhart.audily.core.model.playback
/**
 * Tình trạng phát nhạc hiện tại (Runtime snapshot).
 * Phản ánh chính xác những gì đang diễn ra trong Player.
 */
data class PlaybackState(
    val playerState: PlayerState,
    val currentSongId: Long?,
    val isShuffleOn: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val playbackPosition: Long = 0,
    val duration: Long = 0,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f
) {
    companion object {
        val INITIAL = PlaybackState(
            playerState = PlayerState.IDLE,
            currentSongId = null,
            isShuffleOn = false,
            repeatMode = RepeatMode.OFF,
            playbackPosition = 0,
            duration = 0,
            speed = 1.0f,
            pitch = 1.0f
        )
    }
}
