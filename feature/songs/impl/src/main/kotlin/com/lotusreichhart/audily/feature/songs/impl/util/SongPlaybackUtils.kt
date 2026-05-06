package com.lotusreichhart.audily.feature.songs.impl.util

import com.lotusreichhart.audily.core.designsystem.component.SongPlaybackStatus
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.PlaybackState

/**
 * Xác định trạng thái hiển thị của bài hát dựa trên trạng thái phát nhạc hiện tại.
 */
fun getPlaybackStatus(songId: Long, playbackState: PlaybackState): SongPlaybackStatus {
    val isCurrent = songId == playbackState.currentSongId
    return when {
        !isCurrent -> SongPlaybackStatus.NONE
        playbackState.nowPlayingState == NowPlayingState.PAUSED -> SongPlaybackStatus.PAUSED
        else -> SongPlaybackStatus.PLAYING
    }
}
