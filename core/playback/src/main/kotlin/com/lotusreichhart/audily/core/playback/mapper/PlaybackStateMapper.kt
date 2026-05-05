package com.lotusreichhart.audily.core.playback.mapper

import androidx.media3.common.Player
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playback.RepeatMode

/**
 * Chuyển đổi trạng thái thực tế của Media3 Player sang mô hình PlaybackState của ứng dụng.
 */
internal object PlaybackStateMapper {
    fun map(player: Player, isInitialized: Boolean): PlaybackState {
        val currentMediaItem = player.currentMediaItem
        val songId = currentMediaItem?.mediaId?.toLongOrNull()
        
        return PlaybackState(
            nowPlayingState = when {
                player.playbackState == Player.STATE_ENDED -> NowPlayingState.IDLE
                player.playbackState == Player.STATE_BUFFERING -> NowPlayingState.BUFFERING
                player.isPlaying -> NowPlayingState.PLAYING
                player.playbackState == Player.STATE_READY -> NowPlayingState.PAUSED
                else -> NowPlayingState.IDLE
            },
            currentSongId = songId,
            duration = player.duration,
            isShuffleOn = player.shuffleModeEnabled,
            repeatMode = when (player.repeatMode) {
                Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> RepeatMode.OFF
            },
            queueIds = getQueueIds(player),
            isInitialized = isInitialized
        )
    }

    fun getQueueIds(player: Player): List<Long> {
        val ids = mutableListOf<Long>()
        for (i in 0 until player.mediaItemCount) {
            player.getMediaItemAt(i).mediaId.toLongOrNull()?.let { ids.add(it) }
        }
        return ids
    }
}
