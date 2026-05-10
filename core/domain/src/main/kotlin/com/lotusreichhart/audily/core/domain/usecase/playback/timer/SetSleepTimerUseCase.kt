package com.lotusreichhart.audily.core.domain.usecase.playback.timer

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import javax.inject.Inject

/**
 * UseCase để đặt bộ hẹn giờ tắt nhạc.
 * @param durationMs Thời gian đếm ngược (ms). Truyền 0 để hủy.
 */
class SetSleepTimerUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    operator fun invoke(durationMs: Long) {
        playbackRepository.setSleepTimer(durationMs)
    }
}
