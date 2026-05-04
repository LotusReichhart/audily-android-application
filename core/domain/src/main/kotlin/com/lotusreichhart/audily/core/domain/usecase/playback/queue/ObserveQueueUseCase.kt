package com.lotusreichhart.audily.core.domain.usecase.playback.queue

import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetBasicSongsUseCase
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveQueueUseCase @Inject constructor(
    private val observePlaybackState: ObservePlaybackStateUseCase,
    private val getBasicSongs: GetBasicSongsUseCase,
) {
    operator fun invoke(): Flow<List<Song>> {
        val playbackStateFlow = observePlaybackState()

        // Chỉ tải metadata khi danh sách ID trong hàng đợi thay đổi
        val songsFlow = playbackStateFlow
            .map { it.queueIds }
            .distinctUntilChanged()
            .flatMapLatest { ids ->
                if (ids.isNotEmpty()) getBasicSongs(ids) else flowOf(emptyList())
            }

        return songsFlow
    }
}