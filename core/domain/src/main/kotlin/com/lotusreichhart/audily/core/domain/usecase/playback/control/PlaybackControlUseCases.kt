package com.lotusreichhart.audily.core.domain.usecase.playback.control

import com.lotusreichhart.audily.core.domain.usecase.playback.queue.SkipToIndexUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.settings.SetRepeatModeUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.settings.SetShuffleUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.settings.SetSpeedAndPitchUseCase
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import javax.inject.Inject

/**
 * Facade gộp các hành động điều khiển trình phát.
 */
class PlaybackControlUseCases @Inject constructor(
    private val resumeUseCase: ResumeSongUseCase,
    private val pauseUseCase: PauseSongUseCase,
    private val skipToNextUseCase: SkipToNextUseCase,
    private val skipToPreviousUseCase: SkipToPreviousUseCase,
    private val seekToUseCase: SeekToUseCase,
    private val setShuffleUseCase: SetShuffleUseCase,
    private val setRepeatModeUseCase: SetRepeatModeUseCase,
    private val stopSongUseCase: StopSongUseCase,
    private val setSpeedAndPitchUseCase: SetSpeedAndPitchUseCase,
    private val fastForwardUseCase: FastForwardUseCase,
    private val fastRewindUseCase: FastRewindUseCase,
    private val skipToIndexUseCase: SkipToIndexUseCase
) {
    suspend fun resume() = resumeUseCase()
    suspend fun pause() = pauseUseCase()
    suspend fun skipToNext() = skipToNextUseCase()
    suspend fun skipToPrevious() = skipToPreviousUseCase()
    suspend fun skipToIndex(index: Int) = skipToIndexUseCase(index)
    suspend fun seekTo(position: Long) = seekToUseCase(position)
    suspend fun setShuffle(enabled: Boolean) = setShuffleUseCase(enabled)
    suspend fun setRepeatMode(mode: RepeatMode) = setRepeatModeUseCase(mode)
    suspend fun stop() = stopSongUseCase()
    suspend fun setSpeedAndPitch(speed: Float, pitch: Float) = setSpeedAndPitchUseCase(speed, pitch)
    suspend fun fastForward() = fastForwardUseCase()
    suspend fun fastRewind() = fastRewindUseCase()
}