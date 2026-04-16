package com.lotusreichhart.audily.core.data.repository

import com.lotusreichhart.audily.core.domain.repository.MediaPlayerRepository
import com.lotusreichhart.audily.core.model.song.Song
import javax.inject.Inject

class MediaPlayerRepositoryImpl @Inject constructor(

) : MediaPlayerRepository {
    override suspend fun play(song: Song) {
        TODO("Not yet implemented")
    }

    override suspend fun pause() {
        TODO("Not yet implemented")
    }

    override suspend fun resume() {
        TODO("Not yet implemented")
    }

    override suspend fun stop() {
        TODO("Not yet implemented")
    }
}