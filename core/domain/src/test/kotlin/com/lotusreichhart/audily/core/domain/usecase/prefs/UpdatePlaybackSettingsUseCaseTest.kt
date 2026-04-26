package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.playback.RepeatMode
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

    // === Jump Interval ===

    @Test
    fun `UpdateJumpIntervalUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateJumpInterval(any()) }
        val useCase = UpdateJumpIntervalUseCase(repository)

        useCase(30_000)

        coVerify { repository.updateJumpInterval(30_000) }
    }

    // === Pause On Unplug ===

    @Test
    fun `UpdatePauseOnUnplugUseCase delegates correctly`() = runTest {
        coJustRun { repository.updatePauseOnUnplug(any()) }
        val useCase = UpdatePauseOnUnplugUseCase(repository)

        useCase(false)

        coVerify { repository.updatePauseOnUnplug(false) }
    }

    // === Playback Speed ===

    @Test
    fun `UpdatePlaybackSpeedUseCase delegates correctly`() = runTest {
        coJustRun { repository.updatePlaybackSpeed(any()) }
        val useCase = UpdatePlaybackSpeedUseCase(repository)

        useCase(1.5f)

        coVerify { repository.updatePlaybackSpeed(1.5f) }
    }

    // === Volume Normalization ===

    @Test
    fun `UpdateVolumeNormalizationUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateVolumeNormalization(any()) }
        val useCase = UpdateVolumeNormalizationUseCase(repository)

        useCase(true)

        coVerify { repository.updateVolumeNormalization(true) }
    }

    // === Shuffle ===

    @Test
    fun `UpdateShuffleUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateShuffleEnabled(any()) }
        val useCase = UpdateShuffleUseCase(repository)

        useCase(true)

        coVerify { repository.updateShuffleEnabled(true) }
    }

    // === Repeat Mode ===

    @Test
    fun `UpdateRepeatModeUseCase delegates with ONE`() = runTest {
        coJustRun { repository.updateRepeatMode(any()) }
        val useCase = UpdateRepeatModeUseCase(repository)

        useCase(RepeatMode.ONE)

        coVerify { repository.updateRepeatMode(RepeatMode.ONE) }
    }

    @Test
    fun `UpdateRepeatModeUseCase delegates with ALL`() = runTest {
        coJustRun { repository.updateRepeatMode(any()) }
        val useCase = UpdateRepeatModeUseCase(repository)

        useCase(RepeatMode.ALL)

        coVerify { repository.updateRepeatMode(RepeatMode.ALL) }
    }

    @Test
    fun `UpdateRepeatModeUseCase delegates with OFF`() = runTest {
        coJustRun { repository.updateRepeatMode(any()) }
        val useCase = UpdateRepeatModeUseCase(repository)

        useCase(RepeatMode.OFF)

        coVerify { repository.updateRepeatMode(RepeatMode.OFF) }
    }
}
