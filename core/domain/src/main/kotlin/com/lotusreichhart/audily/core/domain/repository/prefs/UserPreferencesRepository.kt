package com.lotusreichhart.audily.core.domain.repository.prefs

import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import com.lotusreichhart.audily.core.model.prefs.AppTheme
import com.lotusreichhart.audily.core.model.prefs.NowPlayingTheme
import com.lotusreichhart.audily.core.model.prefs.UserPreferences
import com.lotusreichhart.audily.core.model.song.SongSortOrder
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
    suspend fun updateDynamicColor(enabled: Boolean)
    suspend fun updateUseGlassmorphism(enabled: Boolean)

    // === Library Settings ===
    suspend fun updateExcludedFolders(folders: List<String>)
    suspend fun updateMinAudioDuration(duration: Long)
    suspend fun updateFilterSmallFiles(enabled: Boolean)
    suspend fun updateAlbumGridSize(size: Int)

    // Sort preferences
    suspend fun updateSongSortOrder(order: SongSortOrder)
    suspend fun updateSongSortType(type: SortOrderType)

    suspend fun updateAlbumSortOrder(order: AlbumSortOrder)
    suspend fun updateAlbumSortType(type: SortOrderType)

    suspend fun updatePlaylistSortOrder(order: PlaylistSortOrder)
    suspend fun updatePlaylistSortType(type: SortOrderType)

    // === Playback Settings ===
    suspend fun updateSkipDuration(duration: Int)
    suspend fun updatePauseOnUnplug(enabled: Boolean)
    suspend fun updatePlaybackParameters(speed: Float, pitch: Float)
    suspend fun updateVolumeNormalization(enabled: Boolean)
    suspend fun updateShuffleEnabled(enabled: Boolean)
    suspend fun updateRepeatMode(mode: RepeatMode)

    // === Session Persistence (Database) ===
    suspend fun savePlaybackSession(
        songId: Long?,
        position: Long,
        duration: Long,
        queueIds: List<Long>
    )

    suspend fun clearPlaybackSession()

    /**
     * Lấy phiên phát nhạc đã lưu từ Database.
     */
    fun getPlaybackSession(): Flow<com.lotusreichhart.audily.core.model.playback.PlaybackSession?>
}
