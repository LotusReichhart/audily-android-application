package com.lotusreichhart.audily.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.lotusreichhart.audily.core.datastore.preferences.LibraryPreferences
import com.lotusreichhart.audily.core.datastore.preferences.PlaybackPreferences
import com.lotusreichhart.audily.core.datastore.preferences.UiPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.cancelChildren

@OptIn(ExperimentalCoroutinesApi::class)
class AudilyDataStoreTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    private lateinit var dataStore: DataStore<UserPreferencesProto>
    private lateinit var audilyDataStore: AudilyDataStore

    companion object {
        private var fileCounter = 0
    }

    @Before
    fun setup() {
        synchronized(this) {
            fileCounter++
        }
        testDispatcher = UnconfinedTestDispatcher()
        testScope = TestScope(testDispatcher + Job())
        dataStore = DataStoreFactory.create(
            serializer = UserPreferencesSerializer(),
            scope = testScope
        ) {
            java.io.File(tmpFolder.root, "test_user_preferences_$fileCounter.pb")
        }
        audilyDataStore = AudilyDataStore(
            dataStore = dataStore,
            library = LibraryPreferences(dataStore),
            ui = UiPreferences(dataStore),
            playback = PlaybackPreferences(dataStore)
        )
    }

    @After
    fun tearDown() {
        testScope.coroutineContext.cancelChildren()
    }

    // === Library Settings ===

    @Test
    fun `Library - update excluded folders`() = testScope.runTest {
        val folders = listOf("/storage/Ringtones", "/storage/Notifications")
        audilyDataStore.library.updateExcludedFolders(folders)

        val result = audilyDataStore.userPreferences.first()
        assertEquals(folders, result.librarySettings.excludedFoldersList)
    }

    @Test
    fun `Library - update min audio duration`() = testScope.runTest {
        audilyDataStore.library.updateMinAudioDuration(60_000)

        val result = audilyDataStore.userPreferences.first()
        assertEquals(60_000L, result.librarySettings.minAudioDuration)
    }

    @Test
    fun `Library - update filter small files`() = testScope.runTest {
        audilyDataStore.library.updateFilterSmallFiles(true)

        val result = audilyDataStore.userPreferences.first()
        assertTrue(result.librarySettings.filterSmallFiles)
    }

    @Test
    fun `Library - update album grid size`() = testScope.runTest {
        audilyDataStore.library.updateAlbumGridSize(3)

        val result = audilyDataStore.userPreferences.first()
        assertEquals(3, result.librarySettings.albumGridSize)
    }

    // === UI Settings ===

    @Test
    fun `UI - update app theme`() = testScope.runTest {
        audilyDataStore.ui.updateAppTheme(AppThemeProto.APP_THEME_DARK)

        val result = audilyDataStore.userPreferences.first()
        assertEquals(AppThemeProto.APP_THEME_DARK, result.uiSettings.appTheme)
    }

    @Test
    fun `UI - update now playing theme`() = testScope.runTest {
        audilyDataStore.ui.updateNowPlayingTheme(NowPlayingThemeProto.NOW_PLAYING_THEME_BLUR)

        val result = audilyDataStore.userPreferences.first()
        assertEquals(
            NowPlayingThemeProto.NOW_PLAYING_THEME_BLUR,
            result.uiSettings.nowPlayingTheme
        )
    }

    @Test
    fun `UI - update AMOLED black`() = testScope.runTest {
        audilyDataStore.ui.updateUseAmoledBlack(true)

        val result = audilyDataStore.userPreferences.first()
        assertTrue(result.uiSettings.useAmoledBlack)
    }

    @Test
    fun `UI - update accent color with value`() = testScope.runTest {
        audilyDataStore.ui.updateAccentColor(0xFF5722)

        val result = audilyDataStore.userPreferences.first()
        assertTrue(result.uiSettings.hasAccentColor)
        assertEquals(0xFF5722, result.uiSettings.accentColor)
    }

    @Test
    fun `UI - update show mini player extra controls`() = testScope.runTest {
        audilyDataStore.ui.updateShowMiniPlayerExtraControls(false)

        val result = audilyDataStore.userPreferences.first()
        assertFalse(result.uiSettings.showMiniPlayerExtraControls)
    }

    // === Playback Settings ===

    @Test
    fun `Playback - update jump interval`() = testScope.runTest {
        audilyDataStore.playback.updateJumpInterval(30_000)

        val result = audilyDataStore.userPreferences.first()
        assertEquals(30_000, result.playbackSettings.jumpInterval)
    }

    @Test
    fun `Playback - update pause on unplug`() = testScope.runTest {
        audilyDataStore.playback.updatePauseOnUnplug(false)

        val result = audilyDataStore.userPreferences.first()
        assertFalse(result.playbackSettings.pauseOnUnplug)
    }

    @Test
    fun `Playback - update playback speed`() = testScope.runTest {
        audilyDataStore.playback.updatePlaybackSpeed(1.5f)

        val result = audilyDataStore.userPreferences.first()
        assertEquals(1.5f, result.playbackSettings.playbackSpeed, 0.01f)
    }

    @Test
    fun `Playback - update volume normalization`() = testScope.runTest {
        audilyDataStore.playback.updateVolumeNormalization(true)

        val result = audilyDataStore.userPreferences.first()
        assertTrue(result.playbackSettings.volumeNormalization)
    }

    // === Persistent Session ===

    @Test
    fun `Playback - update shuffle enabled`() = testScope.runTest {
        audilyDataStore.playback.updateShuffleEnabled(true)

        val result = audilyDataStore.userPreferences.first()
        assertTrue(result.playbackSettings.isShuffleEnabled)
    }

    @Test
    fun `Playback - update repeat mode`() = testScope.runTest {
        audilyDataStore.playback.updateRepeatMode(RepeatModeProto.REPEAT_MODE_ONE)

        val result = audilyDataStore.userPreferences.first()
        assertEquals(RepeatModeProto.REPEAT_MODE_ONE, result.playbackSettings.repeatMode)
    }

    @Test
    fun `Playback - update last playback session with valid song`() = testScope.runTest {
        val queueIds = listOf(1L, 2L, 3L, 4L, 5L)
        audilyDataStore.playback.updateLastPlaybackSession(
            songId = 42L,
            position = 120_000L,
            queueIds = queueIds
        )

        val result = audilyDataStore.userPreferences.first()
        val playback = result.playbackSettings
        assertTrue(playback.hasLastPlayedSongId)
        assertEquals(42L, playback.lastPlayedSongId)
        assertEquals(120_000L, playback.lastPlaybackPosition)
        assertEquals(queueIds, playback.lastQueueIdsList)
    }
}
