package com.lotusreichhart.audily.core.playback.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.playback.PlaybackManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepositoryImpl @Inject constructor(
    private val playbackManager: PlaybackManager
) : PlaybackRepository {

    override val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun handleEvent(event: PlaybackEvent) {
        playbackManager.handleEvent(event)
    }

    override fun observePlaybackPosition(): Flow<Long> = flow {
        while (true) {
            emit(playbackManager.player.currentPosition)
            delay(500)
        }
    }
}
