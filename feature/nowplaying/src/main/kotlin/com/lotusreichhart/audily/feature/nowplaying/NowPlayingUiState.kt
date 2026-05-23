package com.lotusreichhart.audily.feature.nowplaying

import com.lotusreichhart.audily.core.designsystem.model.UiPalette
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playback.SleepTimerStatus
import com.lotusreichhart.audily.core.model.lyrics.Lyrics
import com.lotusreichhart.audily.core.model.song.Song

data class NowPlayingUiState(
    val playbackState: PlaybackState = PlaybackState.INITIAL,
    val playbackPositionMs: Long = 0,
    val sleepTimerStatus: SleepTimerStatus = SleepTimerStatus.INITIAL,
    val currentSong: Song? = null,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val skipDuration: Int = 10,
    val paletteColors: UiPalette? = null,
    val hasNext: Boolean = true,
    val hasPrevious: Boolean = true,
    val isLyricsVisible: Boolean = false,
    val lyrics: Lyrics? = null,
    val isLyricsLoading: Boolean = false
)