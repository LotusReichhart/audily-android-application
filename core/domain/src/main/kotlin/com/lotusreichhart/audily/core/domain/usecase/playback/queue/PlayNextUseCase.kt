package com.lotusreichhart.audily.core.domain.usecase.playback.queue

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.song.Song
import javax.inject.Inject

/**
 * Thêm một bài hát vào ngay sau bài hát đang phát hiện tại.
 */
class PlayNextUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    suspend operator fun invoke(song: Song) {
        playbackRepository.handleEvent(
            PlaybackEvent.PlayNext(song)
        )
    }
}