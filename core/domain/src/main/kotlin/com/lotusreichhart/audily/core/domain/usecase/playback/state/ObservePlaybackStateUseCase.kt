package com.lotusreichhart.audily.core.domain.usecase.playback.state

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObservePlaybackStateUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    operator fun invoke(): StateFlow<PlaybackState> = playbackRepository.playbackState
}
