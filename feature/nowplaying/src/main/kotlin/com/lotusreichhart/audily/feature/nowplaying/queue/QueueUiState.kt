package com.lotusreichhart.audily.feature.nowplaying.queue

import com.lotusreichhart.audily.core.designsystem.model.UiPalette
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongsSummary

data class QueueUiState(
    val queue: List<Song> = emptyList(),
    val queueSummary: SongsSummary = SongsSummary(),
    val playbackState: PlaybackState = PlaybackState.INITIAL,
    val paletteColors: UiPalette? = null,
    val currentIndex: Int = -1,
    val useGlassmorphism: Boolean = true
)
