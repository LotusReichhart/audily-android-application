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
import com.lotusreichhart.audily.core.model.prefs.AppLanguage
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import android.content.Context
import android.media.MediaScannerConnection
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

internal class UserPreferencesRepositoryImpl @Inject constructor(
    private val audilyDataStore: AudilyDataStore,
    private val playbackDao: PlaybackDao,
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(AudilyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
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

    override suspend fun updateDynamicColor(enabled: Boolean) {
        audilyDataStore.ui.updateDynamicColor(enabled)
    }

    override suspend fun updateUseGlassmorphism(enabled: Boolean) {
        audilyDataStore.ui.updateUseGlassmorphism(enabled)
    }

    override suspend fun updateAppLanguage(language: AppLanguage) {
        audilyDataStore.ui.updateAppLanguage(language.toProto())
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

    override suspend fun updateAutoplayOnHeadphoneConnect(enabled: Boolean) {
        audilyDataStore.playback.updateAutoplayOnHeadphoneConnect(enabled)
    }

    override suspend fun updateAutoplayOnBluetoothConnect(enabled: Boolean) {
        audilyDataStore.playback.updateAutoplayOnBluetoothConnect(enabled)
    }

    override suspend fun updateAudioDucking(enabled: Boolean) {
        audilyDataStore.playback.updateAudioDucking(enabled)
    }

    // === Session Persistence (Database) ===

    override suspend fun savePlaybackSession(
        songId: Long?,
        position: Long,
        duration: Long,
        queueIds: List<Long>
    ) {
        if (queueIds.isEmpty()) {
            Timber.d("Audily Service Kill - UserPreferencesRepository: Blocked saving empty session")
            return
        }

        Timber.d("Audily Service Kill - UserPreferencesRepository: Saving session | Queue size: ${queueIds.size} | SongId: $songId")
        val session = PlaybackSessionEntity(
            currentSongId = songId,
            position = position,
            duration = duration
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

    override suspend fun clearPlayingQueue() {
        playbackDao.clearQueue()
    }

    override fun getPlaybackSession(): Flow<PlaybackSession?> {
        return combine(
            playbackDao.getSession(),
            playbackDao.getQueue()
        ) { session, queue ->
            session?.let {
                PlaybackSession(
                    currentSongId = it.currentSongId,
                    position = it.position,
                    duration = it.duration,
                    queueIds = queue.map { item -> item.songId }
                )
            }
        }
    }

    override suspend fun rescanMediaStore() = withContext(ioDispatcher) {
        val prefs = getUserPreferences().first()
        val excluded = prefs.librarySettings.excludedFolders.map { File(it).canonicalPath }

        val dirs = listOf(
            File("/storage/emulated/0/Music"),
            File("/storage/emulated/0/Download"),
            File("/storage/emulated/0/Audiobooks"),
            File("/storage/emulated/0/Podcasts"),
            File("/storage/emulated/0/Ringtones"),
            File("/storage/emulated/0/Alarms"),
            File("/storage/emulated/0/Notifications")
        )

        val filesToScan = mutableListOf<String>()

        for (dir in dirs) {
            if (!dir.exists() || !dir.isDirectory) continue

            dir.walkTopDown()
                .onEnter { file ->
                    try {
                        val path = file.canonicalPath
                        !excluded.any { path == it || path.startsWith("$it/") }
                    } catch (e: Exception) {
                        false
                    }
                }
                .forEach { file ->
                    if (file.isFile && isAudioFile(file)) {
                        filesToScan.add(file.absolutePath)
                    }
                }
        }

        if (filesToScan.isNotEmpty()) {
            suspendCancellableCoroutine<Unit> { cont ->
                MediaScannerConnection.scanFile(
                    context,
                    filesToScan.toTypedArray(),
                    null
                ) { _, _ -> }
                cont.resume(Unit)
            }
        }
    }

    private fun isAudioFile(file: File): Boolean {
        val extensions = listOf("mp3", "m4a", "flac", "wav", "ogg", "aac", "mid", "wma")
        return extensions.contains(file.extension.lowercase())
    }
}
