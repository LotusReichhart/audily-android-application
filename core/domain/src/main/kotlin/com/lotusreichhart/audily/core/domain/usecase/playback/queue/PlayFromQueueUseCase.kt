package com.lotusreichhart.audily.core.domain.usecase.playback.queue

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class PlayFromQueueUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(songId: Long, queueIds: List<Long>) {
        val currentQueue = playbackRepository.playbackState.value.queueIds

        Timber.d("Audily Service Kill - PlayFromQueueUseCase - currentQueue: $currentQueue")
        Timber.d("Audily Service Kill - PlayFromQueueUseCase - queueIds: $queueIds")

        if (currentQueue == queueIds && currentQueue.isNotEmpty()) {
            val targetIndex = currentQueue.indexOf(songId).coerceAtLeast(0)
            Timber.d("Smart Queue: IDs match, seeking to index $targetIndex")
            playbackRepository.handleEvent(PlaybackEvent.SeekToIndex(targetIndex))
        } else {
            val startTime = System.currentTimeMillis()

            // GIAI ĐOẠN 1: Lấy 1 bài duy nhất và phát ngay lập tức (Instant Play)
            val targetSong = songRepository.getSong(songId).first()
            targetSong?.let {
                Timber.d("Instant Play: Starting target song first")
                playbackRepository.handleEvent(PlaybackEvent.SetQueue(listOf(it), 0))
            }

            // GIAI ĐOẠN 2: Lấy toàn bộ queue trong background và chèn hai đầu (Smart Queue)
            val songs = songRepository.getSongs(queueIds).first()
            val indexInFull = songs.indexOfFirst { it.id == songId }

            if (indexInFull != -1) {
                val before = songs.take(indexInFull)
                val after = songs.drop(indexInFull + 1)

                // Chèn phần trước vào vị trí 0 (đẩy bài đang phát xuống)
                if (before.isNotEmpty()) {
                    playbackRepository.handleEvent(PlaybackEvent.AddSongsToQueue(before, 0))
                }

                // Chèn phần sau vào cuối danh sách
                if (after.isNotEmpty()) {
                    playbackRepository.handleEvent(PlaybackEvent.AddSongsToQueue(after))
                }

                val duration = System.currentTimeMillis() - startTime
                Timber.d("PlayFromQueue: Orchestrated ${songs.size} songs in ${duration}ms")
            }
        }
    }
}
