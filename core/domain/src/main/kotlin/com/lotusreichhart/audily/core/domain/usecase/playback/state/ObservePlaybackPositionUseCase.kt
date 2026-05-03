package com.lotusreichhart.audily.core.domain.usecase.playback.state

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlaybackPositionUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    operator fun invoke(): Flow<Long> = playbackRepository.observePlaybackPosition()
}
