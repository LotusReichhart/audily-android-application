package com.lotusreichhart.audily.core.model.playback

import com.lotusreichhart.audily.core.model.song.Song

/**
 * Model gộp chứa toàn bộ dữ liệu cần thiết cho màn hình Now Playing.
 * Được làm giàu (enriched) từ PlaybackState thô.
 */
data class NowPlayingData(
    val song: Song?,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val playbackState: PlaybackState,
    val colors: PaletteColors?,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
    val skipDuration: Int
)
