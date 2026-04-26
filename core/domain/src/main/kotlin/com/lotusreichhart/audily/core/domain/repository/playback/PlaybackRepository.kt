package com.lotusreichhart.audily.core.domain.repository.playback

import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import kotlinx.coroutines.flow.StateFlow

interface PlaybackRepository {
    val playbackState: StateFlow<PlaybackState>

    /**
     * Điểm nhận lệnh tập trung cho trình phát nhạc.
     */
    suspend fun handleEvent(event: PlaybackEvent)
}