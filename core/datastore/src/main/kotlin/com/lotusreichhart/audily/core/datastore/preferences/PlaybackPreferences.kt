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

    suspend fun updateSkipDuration(duration: Int) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setSkipDuration(duration)
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

    suspend fun updatePlaybackParameters(speed: Float, pitch: Float) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setPlaybackSettings(
                    prefs.playbackSettings.toBuilder()
                        .setPlaybackSpeed(speed)
                        .setPlaybackPitch(pitch)
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
}