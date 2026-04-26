package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SavePlaybackSessionUseCaseTest {

    private lateinit var repository: UserPreferencesRepository
    private lateinit var useCase: SavePlaybackSessionUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SavePlaybackSessionUseCase(repository)
    }

    @Test
    fun `invoke delegates with valid songId`() = runTest {
        coJustRun { repository.savePlaybackSession(any(), any(), any()) }

        useCase(songId = 42L, position = 120_000L, queueIds = listOf(1L, 2L, 3L))

        coVerify { repository.savePlaybackSession(42L, 120_000L, listOf(1L, 2L, 3L)) }
    }

    @Test
    fun `invoke delegates with null songId`() = runTest {
        coJustRun { repository.savePlaybackSession(any(), any(), any()) }

        useCase(songId = null, position = 0L, queueIds = emptyList())

        coVerify { repository.savePlaybackSession(null, 0L, emptyList()) }
    }
}
