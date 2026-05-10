package com.lotusreichhart.audily.core.data.repository.prefs

import com.lotusreichhart.audily.core.data.mapper.prefs.toDomain
import com.lotusreichhart.audily.core.data.mapper.prefs.toProto
import com.lotusreichhart.audily.core.database.dao.PlaybackDao
import com.lotusreichhart.audily.core.database.entity.PlaybackSessionEntity
import com.lotusreichhart.audily.core.database.entity.PlayingQueueEntity
import com.lotusreichhart.audily.core.datastore.AudilyDataStore
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playback.PlaybackSession
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import com.lotusreichhart.audily.core.model.prefs.AppTheme
import com.lotusreichhart.audily.core.model.prefs.NowPlayingTheme
import com.lotusreichhart.audily.core.model.prefs.UserPreferences
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class UserPreferencesRepositoryImpl @Inject constructor(
    private val audilyDataStore: AudilyDataStore,
    private val playbackDao: PlaybackDao
) : UserPreferencesRepository {

    override fun getUserPreferences(): Flow<UserPreferences> {
        return audilyDataStore.userPreferences.map { it.toDomain() }
    }

    // === UI Settings ===

    override suspend fun updateAppTheme(theme: AppTheme) {
        audilyDataStore.ui.updateAppTheme(theme.toProto())
    }

    override suspend fun updateNowPlayingTheme(theme: NowPlayingTheme) {
        audilyDataStore.ui.updateNowPlayingTheme(theme.toProto())
    }

    override suspend fun updateUseAmoledBlack(enabled: Boolean) {
        audilyDataStore.ui.updateUseAmoledBlack(enabled)
    }

    override suspend fun updateAccentColor(color: Int?) {
        audilyDataStore.ui.updateAccentColor(color)
    }

    override suspend fun updateShowMiniPlayerExtraControls(show: Boolean) {
        audilyDataStore.ui.updateShowMiniPlayerExtraControls(show)
    }

    // === Library Settings ===

    override suspend fun updateExcludedFolders(folders: List<String>) {
        audilyDataStore.library.updateExcludedFolders(folders)
    }

    override suspend fun updateMinAudioDuration(duration: Long) {
        audilyDataStore.library.updateMinAudioDuration(duration)
    }

    override suspend fun updateFilterSmallFiles(enabled: Boolean) {
        audilyDataStore.library.updateFilterSmallFiles(enabled)
    }

    override suspend fun updateAlbumGridSize(size: Int) {
        audilyDataStore.library.updateAlbumGridSize(size)
    }

    override suspend fun updateSongSortOrder(order: SongSortOrder) {
        audilyDataStore.library.updateSongSortOrder(order.toProto())
    }

    override suspend fun updateSongSortType(type: SortOrderType) {
        audilyDataStore.library.updateSongSortType(type.toProto())
    }

    override suspend fun updateAlbumSortOrder(order: AlbumSortOrder) {
        audilyDataStore.library.updateAlbumSortOrder(order.toProto())
    }

    override suspend fun updateAlbumSortType(type: SortOrderType) {
        audilyDataStore.library.updateAlbumSortType(type.toProto())
    }

    override suspend fun updatePlaylistSortOrder(order: PlaylistSortOrder) {
        audilyDataStore.library.updatePlaylistSortOrder(order.toProto())
    }

    override suspend fun updatePlaylistSortType(type: SortOrderType) {
        audilyDataStore.library.updatePlaylistSortType(type.toProto())
    }

    // === Playback Settings ===

    override suspend fun updateSkipDuration(duration: Int) {
        audilyDataStore.playback.updateSkipDuration(duration)
    }

    override suspend fun updatePauseOnUnplug(enabled: Boolean) {
        audilyDataStore.playback.updatePauseOnUnplug(enabled)
    }

    override suspend fun updatePlaybackParameters(speed: Float, pitch: Float) {
        audilyDataStore.playback.updatePlaybackParameters(speed, pitch)
    }

    override suspend fun updateVolumeNormalization(enabled: Boolean) {
        audilyDataStore.playback.updateVolumeNormalization(enabled)
    }

    override suspend fun updateShuffleEnabled(enabled: Boolean) {
        audilyDataStore.playback.updateShuffleEnabled(enabled)
    }

    override suspend fun updateRepeatMode(mode: RepeatMode) {
        audilyDataStore.playback.updateRepeatMode(mode.toProto())
    }

    // === Session Persistence (Database) ===

    override suspend fun savePlaybackSession(
        songId: Long?,
        position: Long,
        duration: Long,
        queueIds: List<Long>,
        sourceId: Long?,
        sourceType: String?
    ) {
        if (queueIds.isEmpty()) {
            Timber.d("Audily Service Kill - UserPreferencesRepository: Blocked saving empty session")
            return
        }

        Timber.d("Audily Service Kill - UserPreferencesRepository: Saving session | Queue size: ${queueIds.size} | SongId: $songId")
        val session = PlaybackSessionEntity(
            currentSongId = songId,
            position = position,
            duration = duration,
            sourceId = sourceId,
            sourceType = sourceType
        )
        val queueItems = queueIds.mapIndexed { index, id ->
            PlayingQueueEntity(
                songId = id,
                orderIndex = index
            )
        }
        playbackDao.saveFullPlaybackState(session, queueItems)
    }

    override suspend fun clearPlaybackSession() {
        playbackDao.clearSession()
    }

    override fun getPlaybackSession(): Flow<PlaybackSession?> {
        return kotlinx.coroutines.flow.combine(
            playbackDao.getSession(),
            playbackDao.getQueue()
        ) { session, queue ->
            session?.let {
                PlaybackSession(
                    currentSongId = it.currentSongId,
                    position = it.position,
                    duration = it.duration,
                    queueIds = queue.map { item -> item.songId },
                    sourceId = it.sourceId,
                    sourceType = it.sourceType
                )
            }
        }
    }
}
