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

    override suspend fun onPositionDiscontinuity(songId: Long?, position: Long, queueIds: List<Long>) {
        userPreferencesRepository.savePlaybackSession(songId, position, queueIds)
    }

    override suspend fun onPlaybackStateChanged(isPlaying: Boolean, songId: Long?, position: Long, queueIds: List<Long>) {
        userPreferencesRepository.savePlaybackSession(songId, position, queueIds)
    }

    override suspend fun onSessionEnded(songId: Long?, position: Long, queueIds: List<Long>) {
        userPreferencesRepository.savePlaybackSession(songId, position, queueIds)
    }
}
