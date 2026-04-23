package com.lotusreichhart.audily.core.data.repository.playback

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.song.Song
import javax.inject.Inject

internal class PlaybackRepositoryImpl @Inject constructor(

) : PlaybackRepository {
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