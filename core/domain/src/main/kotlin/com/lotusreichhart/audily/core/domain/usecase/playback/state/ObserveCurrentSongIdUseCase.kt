package com.lotusreichhart.audily.core.domain.usecase.playback.state

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveCurrentSongIdUseCase @Inject constructor(
    private val observePlaybackState: ObservePlaybackStateUseCase
) {
    operator fun invoke(): Flow<Long?> = observePlaybackState()
        .map { it.currentSongId }
        .distinctUntilChanged()
}