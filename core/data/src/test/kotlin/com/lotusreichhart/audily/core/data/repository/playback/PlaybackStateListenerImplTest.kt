package com.lotusreichhart.audily.core.data.repository.playback

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlaybackStateListenerImplTest {

    private val userPreferencesRepository: UserPreferencesRepository = mockk(relaxed = true)
    private lateinit var listener: PlaybackStateListenerImpl

    @Before
    fun setup() {
        listener = PlaybackStateListenerImpl(userPreferencesRepository)
    }

    @Test
    fun `onPositionDiscontinuity should call userPreferencesRepository savePlaybackSession`() = runTest {
        val songId = 1L
        val position = 1000L
        val queueIds = listOf(1L, 2L)
        
        listener.onPositionDiscontinuity(songId, position, queueIds)
        
        coVerify { userPreferencesRepository.savePlaybackSession(songId, position, queueIds) }
    }

    @Test
    fun `onPlaybackStateChanged should call userPreferencesRepository savePlaybackSession`() = runTest {
        val songId = 1L
        val position = 1000L
        val queueIds = listOf(1L, 2L)
        
        listener.onPlaybackStateChanged(true, songId, position, queueIds)
        
        coVerify { userPreferencesRepository.savePlaybackSession(songId, position, queueIds) }
    }

    @Test
    fun `onSessionEnded should call userPreferencesRepository savePlaybackSession`() = runTest {
        val songId = 1L
        val position = 1000L
        val queueIds = listOf(1L, 2L)
        
        listener.onSessionEnded(songId, position, queueIds)
        
        coVerify { userPreferencesRepository.savePlaybackSession(songId, position, queueIds) }
    }
}
