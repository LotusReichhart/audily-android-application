package com.lotusreichhart.audily.core.domain.repository

import com.lotusreichhart.audily.core.model.song.Song


interface MediaPlayerRepository {
    suspend fun play(song: Song)
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
}
