package com.lotusreichhart.audily.core.designsystem.util

import com.lotusreichhart.audily.core.designsystem.component.SongPlaybackStatus
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.PlaybackState

fun getSongPlaybackStatus(songId: Long, playbackState: PlaybackState): SongPlaybackStatus {
    val isCurrent = songId == playbackState.currentSongId
    return when {
        !isCurrent -> SongPlaybackStatus.NONE
        playbackState.nowPlayingState == NowPlayingState.PAUSED -> SongPlaybackStatus.PAUSED
        else -> SongPlaybackStatus.PLAYING
    }
}