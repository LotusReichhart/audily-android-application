package com.lotusreichhart.audily.feature.nowplaying

import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song

data class NowPlayingUiState(
    val playbackState: PlaybackState = PlaybackState.INITIAL,
    val currentSong: Song? = null,
    val jumpInterval: Int = 10000
)
