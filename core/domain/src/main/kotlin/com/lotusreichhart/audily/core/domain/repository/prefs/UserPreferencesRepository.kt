package com.lotusreichhart.audily.core.domain.repository.prefs

import com.lotusreichhart.audily.core.model.prefs.AppTheme
import com.lotusreichhart.audily.core.model.prefs.NowPlayingTheme
import com.lotusreichhart.audily.core.model.prefs.UserPreferences
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    /**
     * Luồng phản ứng của toàn bộ tùy chọn người dùng.
     */
    fun getUserPreferences(): Flow<UserPreferences>

    // === UI Settings ===
    suspend fun updateAppTheme(theme: AppTheme)
    suspend fun updateNowPlayingTheme(theme: NowPlayingTheme)
    suspend fun updateUseAmoledBlack(enabled: Boolean)
    suspend fun updateAccentColor(color: Int?)
    suspend fun updateShowMiniPlayerExtraControls(show: Boolean)

    // === Library Settings ===
    suspend fun updateExcludedFolders(folders: List<String>)
    suspend fun updateMinAudioDuration(duration: Long)
    suspend fun updateFilterSmallFiles(enabled: Boolean)
    suspend fun updateAlbumGridSize(size: Int)

    // === Playback Settings ===
    suspend fun updateJumpInterval(interval: Int)
    suspend fun updatePauseOnUnplug(enabled: Boolean)
    suspend fun updatePlaybackSpeed(speed: Float)
    suspend fun updateVolumeNormalization(enabled: Boolean)
    suspend fun updateShuffleEnabled(enabled: Boolean)
    suspend fun updateRepeatMode(mode: RepeatMode)

    // === Session Persistence ===
    suspend fun savePlaybackSession(songId: Long?, position: Long, queueIds: List<Long>)
}
