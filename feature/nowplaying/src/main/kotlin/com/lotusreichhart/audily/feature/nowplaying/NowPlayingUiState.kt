package com.lotusreichhart.audily.feature.nowplaying

import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.nowplaying.model.NowPlayingPaletteColors

data class NowPlayingUiState(
    val playbackState: PlaybackState = PlaybackState.INITIAL,
    val playbackPositionMs: Long = 0,
    val currentSong: Song? = null,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val skipDuration: Int = 10,
    val paletteColors: NowPlayingPaletteColors? = null,
    val hasNext: Boolean = true,
    val hasPrevious: Boolean = true,
    val isLyricsVisible: Boolean = false
)