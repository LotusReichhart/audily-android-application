package com.lotusreichhart.audily.core.domain.repository.playback

import com.lotusreichhart.audily.core.model.song.Song

interface PlaybackRepository {
    suspend fun play(song: Song)
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
}