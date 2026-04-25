package com.lotusreichhart.audily.core.datastore.preferences

import androidx.datastore.core.DataStore
import com.lotusreichhart.audily.core.datastore.RepeatModeProto
import com.lotusreichhart.audily.core.datastore.UserPreferencesProto
import javax.inject.Inject

/**
 * Quản lý các thiết lập trình phát nhạc và trạng thái phiên phát nhạc (Resume).
 */
class PlaybackPreferences @Inject constructor(
    private val dataStore: DataStore<UserPreferencesProto>
) {
    // === Playback Settings ===

    suspend fun updateJumpInterval(interval: Int) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setJumpInterval(interval)
                )
                .build()
        }
    }

    suspend fun updatePauseOnUnplug(enabled: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setPauseOnUnplug(enabled)
                )
                .build()
        }
    }

    suspend fun updatePlaybackSpeed(speed: Float) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setPlaybackSpeed(speed)
                )
                .build()
        }
    }

    suspend fun updateVolumeNormalization(enabled: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setVolumeNormalization(enabled)
                )
                .build()
        }
    }

    // === Persistent Session (Resume) ===

    suspend fun updateShuffleEnabled(enabled: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setIsShuffleEnabled(enabled)
                )
                .build()
        }
    }

    suspend fun updateRepeatMode(mode: RepeatModeProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setRepeatMode(mode)
                )
                .build()
        }
    }

    /**
     * Lưu trạng thái phát nhạc hiện tại để phục hồi sau khi app bị kill.
     * Nên gọi khi: Pause, mỗi 10s đang phát, hoặc khi thay đổi Queue.
     */
    suspend fun updateLastPlaybackSession(
        songId: Long?,
        position: Long,
        queueIds: List<Long>
    ) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setHasLastPlayedSongId(songId != null)
                        .setLastPlayedSongId(songId ?: 0)
                        .setLastPlaybackPosition(position)
                        .clearLastQueueIds()
                        .addAllLastQueueIds(queueIds)
                )
                .build()
        }
    }
}