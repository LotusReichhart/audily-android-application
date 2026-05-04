package com.lotusreichhart.audily.core.domain.usecase.playback.queue

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import javax.inject.Inject

/**
 * Di chuyển trình phát đến một vị trí (index) cụ thể trong hàng đợi hiện tại.
 */
class SkipToIndexUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    suspend operator fun invoke(index: Int) {
        playbackRepository.handleEvent(PlaybackEvent.SeekToIndex(index))
    }
}