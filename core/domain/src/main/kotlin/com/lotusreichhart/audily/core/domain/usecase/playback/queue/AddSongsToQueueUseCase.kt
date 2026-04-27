package com.lotusreichhart.audily.core.domain.usecase.playback.queue

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.song.Song
import javax.inject.Inject

/**
 * UseCase thêm danh sách bài hát vào cuối hàng đợi hiện tại.
 */
class AddSongsToQueueUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {
    suspend operator fun invoke(songs: List<Song>) {
        playbackRepository.handleEvent(PlaybackEvent.AddSongsToQueue(songs))
    }
}
