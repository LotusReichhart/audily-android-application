package com.lotusreichhart.audily.core.domain.usecase.playback.state

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * UseCase chịu trách nhiệm khôi phục trạng thái phát nhạc từ DataStore.
 * Nó kết nối dữ liệu từ UserPreferences (DataStore) và SongRepository (MediaStore)
 * để đẩy vào PlaybackRepository (ExoPlayer).
 */
class RestorePlaybackSessionUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val songRepository: SongRepository
) {
    suspend operator fun invoke() {
        val userPrefs = userPreferencesRepository.getUserPreferences().first()
        val lastState = userPrefs.playbackSettings
        
        if (lastState.lastQueueIds.isNotEmpty()) {
            val songs = songRepository.getSongs(lastState.lastQueueIds).first()
            
            if (songs.isNotEmpty()) {
                playbackRepository.handleEvent(
                    PlaybackEvent.PlayFromQueue(
                        songId = lastState.lastPlayedSongId ?: songs.first().id,
                        queueIds = lastState.lastQueueIds
                    )
                )
                
                // Khôi phục các thiết lập khác
                playbackRepository.handleEvent(PlaybackEvent.SetShuffle(lastState.isShuffleEnabled))
                playbackRepository.handleEvent(PlaybackEvent.SetRepeatMode(lastState.repeatMode))
                playbackRepository.handleEvent(PlaybackEvent.SeekTo(lastState.lastPlaybackPosition))
                
                // Đảm bảo dừng nhạc khi vừa mở app
                playbackRepository.handleEvent(PlaybackEvent.Pause)
            }
        }
    }
}
