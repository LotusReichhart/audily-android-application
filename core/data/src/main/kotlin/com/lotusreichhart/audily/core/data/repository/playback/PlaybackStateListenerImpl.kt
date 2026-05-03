package com.lotusreichhart.audily.core.data.repository.playback

import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackStateListener
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import javax.inject.Inject

/**
 * Thực thi việc lắng nghe trạng thái phát nhạc và lưu vào DataStore.
 */
class PlaybackStateListenerImpl @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : PlaybackStateListener {

    override suspend fun onPositionDiscontinuity(
        songId: Long?,
        position: Long,
        duration: Long,
        queueIds: List<Long>,
        sourceId: Long?,
        sourceType: String?
    ) {
        userPreferencesRepository.savePlaybackSession(songId, position, duration, queueIds, sourceId, sourceType)
    }

    override suspend fun onPlaybackStateChanged(
        isPlaying: Boolean,
        songId: Long?,
        position: Long,
        duration: Long,
        queueIds: List<Long>,
        sourceId: Long?,
        sourceType: String?
    ) {
        userPreferencesRepository.savePlaybackSession(songId, position, duration, queueIds, sourceId, sourceType)
    }

    override suspend fun onSessionEnded(
        songId: Long?,
        position: Long,
        duration: Long,
        queueIds: List<Long>,
        sourceId: Long?,
        sourceType: String?
    ) {
        userPreferencesRepository.savePlaybackSession(songId, position, duration, queueIds, sourceId, sourceType)
    }
}
