package com.lotusreichhart.audily.core.playback.repository

import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.playback.PlaybackManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlaybackRepositoryImplTest {

    private val playbackManager: PlaybackManager = mockk(relaxed = true)
    private val songRepository: SongRepository = mockk()
    
    // Ta không initialize ở setup để có thể stub playbackManager.playbackState trước
    private lateinit var repository: PlaybackRepositoryImpl

    @Before
    fun setup() {
        // Mặc định trả về INITIAL state để tránh lỗi khi khởi tạo Repository
        every { playbackManager.playbackState } returns MutableStateFlow(PlaybackState.INITIAL)
    }

    private fun initRepository() {
        repository = PlaybackRepositoryImpl(
            playbackManager = playbackManager,
            songRepository = songRepository
        )
    }

    @Test
    fun `playbackState should reflect playbackManager playbackState`() = runTest {
        val expectedState = PlaybackState.INITIAL.copy(currentSongId = 123L)
        every { playbackManager.playbackState } returns MutableStateFlow(expectedState)

        initRepository()
        assertEquals(expectedState, repository.playbackState.value)
    }

    @Test
    fun `handleEvent PlayFromQueue should resolve songs and send SetQueue to manager`() = runTest {
        initRepository()
        
        val song1 = createTestSong(1L)
        val song2 = createTestSong(2L)
        val songs = listOf(song1, song2)
        val queueIds = listOf(1L, 2L)
        
        coEvery { songRepository.getSongs(queueIds) } returns flowOf(songs)
        
        repository.handleEvent(PlaybackEvent.PlayFromQueue(songId = 2L, queueIds = queueIds))
        
        coVerify { 
            playbackManager.handleEvent(match { 
                it is PlaybackEvent.SetQueue && it.songs == songs && it.startIndex == 1
            }) 
        }
    }

    @Test
    fun `handleEvent Pause should delegate directly to manager`() = runTest {
        initRepository()
        repository.handleEvent(PlaybackEvent.Pause)
        coVerify { playbackManager.handleEvent(PlaybackEvent.Pause) }
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
