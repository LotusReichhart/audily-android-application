package com.lotusreichhart.audily.core.domain.usecase.playback.queue

import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetRemainingQueueSummaryUseCase @Inject constructor(
    private val observeQueue: ObserveQueueUseCase,
    private val observePlaybackState: ObservePlaybackStateUseCase
) {
    operator fun invoke(): Flow<SongsSummary> {
        return combine(
            observeQueue(),
            observePlaybackState()
        ) { queue, playbackState ->
            val currentSongId = playbackState.currentSongId
            val currentIndex = queue.indexOfFirst { it.id == currentSongId }
            if (currentIndex == -1) {
                SongsSummary(0, 0L)
            } else {
                // Tính toán từ vị trí hiện tại đến hết danh sách
                val remainingSongs = queue.drop(currentIndex)
                SongsSummary(
                    totalCount = remainingSongs.size,
                    totalDuration = remainingSongs.sumOf { it.basic.duration }
                )
            }
        }
    }
}