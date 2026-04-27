package com.lotusreichhart.audily.core.domain.usecase.playback.control

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import javax.inject.Inject

/**
 * UseCase thực hiện việc tua nhanh theo khoảng cách (jump interval) trong settings.
 */
class FastForwardUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    suspend operator fun invoke() = playbackRepository.handleEvent(PlaybackEvent.FastForward)
}
