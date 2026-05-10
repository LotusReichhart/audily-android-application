package com.lotusreichhart.audily.core.playback.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers.Main
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playback.SleepTimerStatus
import com.lotusreichhart.audily.core.playback.PlaybackManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PlaybackRepositoryImpl @Inject constructor(
    private val playbackManager: PlaybackManager,
    @param:Dispatcher(Main) private val mainDispatcher: CoroutineDispatcher,
) : PlaybackRepository {

    override val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState
    override val sleepTimerStatus: StateFlow<SleepTimerStatus> = playbackManager.sleepTimerStatus

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun handleEvent(event: PlaybackEvent) {
        playbackManager.handleEvent(event)
    }

    override fun observePlaybackPosition(): Flow<Long> = flow {
        while (true) {
            val position = withContext(mainDispatcher) {
                playbackManager.player.currentPosition
            }
            emit(position)
            delay(250) // Đảm bảo độ mượt cho Progress Bar
        }
    }

    override fun markAsInitialized() {
        playbackManager.markAsInitialized()
    }

    override fun setRestoring(restoring: Boolean) {
        playbackManager.setRestoring(restoring)
    }

    override fun setSleepTimer(durationMs: Long) {
        playbackManager.setSleepTimer(durationMs)
    }

    override fun needsRestoration(): Boolean = playbackManager.needsRestoration()
}
