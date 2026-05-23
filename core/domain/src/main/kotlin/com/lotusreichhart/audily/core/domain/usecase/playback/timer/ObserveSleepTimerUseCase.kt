package com.lotusreichhart.audily.core.domain.usecase.playback.timer

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.SleepTimerStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * UseCase để theo dõi trạng thái và thời gian còn lại của bộ hẹn giờ.
 */
class ObserveSleepTimerUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    operator fun invoke(): StateFlow<SleepTimerStatus> {
        return playbackRepository.sleepTimerStatus
    }
}
