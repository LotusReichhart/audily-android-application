package com.lotusreichhart.audily.core.playback

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackStateListener
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.Song
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PlaybackManagerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val listener: PlaybackStateListener = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var playbackManager: PlaybackManager

    @Before
    fun setup() {
        playbackManager = PlaybackManager(
            context = context,
            listeners = setOf(listener),
            mainDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        playbackManager.release()
    }

    @Test
    fun `handleEvent SetQueue should prepare and play the player`() = runTest {
        val song = createTestSong(1L)
        
        playbackManager.handleEvent(PlaybackEvent.SetQueue(listOf(song), 0))

        val player = playbackManager.player
        assertEquals(1, player.mediaItemCount)
        assertEquals("1", player.getMediaItemAt(0).mediaId)
    }

    @Test
    fun `handleEvent Pause should pause the player`() {
        playbackManager.handleEvent(PlaybackEvent.Pause)
        assertFalse(playbackManager.player.playWhenReady)
    }

    @Test
    fun `handleEvent RemoveFromQueue should remove items correctly`() {
        val song1 = createTestSong(1L)
        val song2 = createTestSong(2L)
        playbackManager.handleEvent(PlaybackEvent.SetQueue(listOf(song1, song2), 0))
        
        playbackManager.handleEvent(PlaybackEvent.RemoveFromQueue(1L))
        
        assertEquals(1, playbackManager.player.mediaItemCount)
        assertEquals("2", playbackManager.player.getMediaItemAt(0).mediaId)
    }

    @Test
    fun `handleEvent MoveQueueItem should swap items correctly`() {
        val song1 = createTestSong(1L)
        val song2 = createTestSong(2L)
        playbackManager.handleEvent(PlaybackEvent.SetQueue(listOf(song1, song2), 0))
        
        playbackManager.handleEvent(PlaybackEvent.MoveQueueItem(0, 1))
        
        assertEquals("2", playbackManager.player.getMediaItemAt(0).mediaId)
        assertEquals("1", playbackManager.player.getMediaItemAt(1).mediaId)
    }

    @Test
    fun `handleEvent AddSongsToQueue should append items correctly`() {
        val song1 = createTestSong(1L)
        val song2 = createTestSong(2L)
        playbackManager.handleEvent(PlaybackEvent.SetQueue(listOf(song1), 0))
        
        playbackManager.handleEvent(PlaybackEvent.AddSongsToQueue(listOf(song2)))
        
        assertEquals(2, playbackManager.player.mediaItemCount)
        assertEquals("1", playbackManager.player.getMediaItemAt(0).mediaId)
        assertEquals("2", playbackManager.player.getMediaItemAt(1).mediaId)
    }

    @Test
    fun `onSessionEnded should notify listeners with current state`() = runTest {
        playbackManager.player 
        
        playbackManager.onSessionEnded()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { listener.onSessionEnded(any(), any(), any()) }
    }

    private fun createTestSong(id: Long) = Song(
        id = id,
        basic = BasicSongMetadata(
            title = "Title $id",
            artist = "Artist $id",
            album = "Album $id",
            albumId = 1L,
            duration = 1000L,
            path = "file:///test$id.mp3"
        )
    )
}
