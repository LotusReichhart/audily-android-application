package com.lotusreichhart.audily.core.domain.usecase.playback.state

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import kotlinx.coroutines.flow.first
import timber.log.Timber
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
        // QUAN TRỌNG: Nếu Player đang có bài hát và KHÔNG ở trạng thái cần khôi phục (Zombie),
        // thì mới bỏ qua việc khôi phục để tránh ngắt quãng nhạc.
        if (playbackRepository.playbackState.value.currentSongId != null && !playbackRepository.needsRestoration()) {
            Timber.d("Audily Service Kill - Active session detected, skipping restoration")
            playbackRepository.markAsInitialized()
            return
        }

        try {
            playbackRepository.setRestoring(true)
            val session = userPreferencesRepository.getPlaybackSession().first()
            if (session == null || session.queueIds.isEmpty()) return

            val userPrefs = userPreferencesRepository.getUserPreferences().first()
            val settings = userPrefs.playbackSettings

            val songs = songRepository.getSongsByIds(session.queueIds).first()

            if (songs.isNotEmpty()) {
                val startIndex = session.currentSongId?.let { id ->
                    songs.indexOfFirst { it.id == id }
                }?.coerceAtLeast(0) ?: 0

                playbackRepository.handleEvent(
                    PlaybackEvent.SetQueue(
                        songs = songs,
                        startIndex = startIndex,
                        startPosition = session.position
                    )
                )

                // Khôi phục các thiết lập khác
                playbackRepository.handleEvent(PlaybackEvent.SetShuffle(settings.isShuffleEnabled))
                playbackRepository.handleEvent(PlaybackEvent.SetRepeatMode(settings.repeatMode))

                // Đảm bảo dừng nhạc khi vừa mở app
                playbackRepository.handleEvent(PlaybackEvent.Pause)
            }
        } finally {
            playbackRepository.setRestoring(false)
        }
    }
}
