package com.lotusreichhart.audily.core.domain.usecase.playback.queue

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import javax.inject.Inject

class PlayFromQueueUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    suspend operator fun invoke(songId: Long, queueIds: List<Long>) = 
        playbackRepository.handleEvent(PlaybackEvent.PlayFromQueue(songId, queueIds))
}
