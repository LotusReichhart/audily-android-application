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
}
