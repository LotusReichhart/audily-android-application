package com.lotusreichhart.audily.core.domain.usecase.playback.control

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import javax.inject.Inject

/**
 * Dừng hoàn toàn trình phát nhạc, xóa hàng đợi và ẩn giao diện Now Playing.
 */
class StopSongUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    suspend operator fun invoke() = playbackRepository.handleEvent(PlaybackEvent.Stop)
}
