package com.lotusreichhart.audily.core.playback.repository

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.playback.PlaybackManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepositoryImpl @Inject constructor(
    private val playbackManager: PlaybackManager,
    private val songRepository: SongRepository
) : PlaybackRepository {

    override val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    override suspend fun handleEvent(event: PlaybackEvent) {
        when (event) {
            is PlaybackEvent.PlayFromQueue -> {
                val songs = songRepository.getSongs(event.queueIds).first()
                val startIndex = songs.indexOfFirst { it.id == event.songId }.coerceAtLeast(0)
                // Gửi event đã được resolve dữ liệu xuống Manager
                playbackManager.handleEvent(PlaybackEvent.SetQueue(songs, startIndex))
            }
            // Với các event khác, ta chuyển tiếp trực tiếp xuống manager
            else -> playbackManager.handleEvent(event)
        }
    }
}
