package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Tests cho tất cả Playback Settings UseCases.
 */
class UpdatePlaybackSettingsUseCaseTest {

    private lateinit var repository: UserPreferencesRepository

    @Before
    fun setup() {
        repository = mockk()
    }

    // === Skip Duration ===

    @Test
    fun `UpdateSkipDurationUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateSkipDuration(any()) }
        val useCase = UpdateSkipDurationUseCase(repository)

        useCase(30_000)

        coVerify { repository.updateSkipDuration(30_000) }
    }

    // === Pause On Unplug ===

    @Test
    fun `UpdatePauseOnUnplugUseCase delegates correctly`() = runTest {
        coJustRun { repository.updatePauseOnUnplug(any()) }
        val useCase = UpdatePauseOnUnplugUseCase(repository)

        useCase(false)

        coVerify { repository.updatePauseOnUnplug(false) }
    }


    // === Volume Normalization ===

    @Test
    fun `UpdateVolumeNormalizationUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateVolumeNormalization(any()) }
        val useCase = UpdateVolumeNormalizationUseCase(repository)

        useCase(true)

        coVerify { repository.updateVolumeNormalization(true) }
    }

}
