package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.prefs.AppTheme
import com.lotusreichhart.audily.core.model.prefs.NowPlayingTheme
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Tests cho tất cả UI Settings UseCases.
 */
class UpdateUiSettingsUseCaseTest {

    private lateinit var repository: UserPreferencesRepository

    @Before
    fun setup() {
        repository = mockk()
    }

    // === AppTheme ===

    @Test
    fun `UpdateAppThemeUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateAppTheme(any()) }
        val useCase = UpdateAppThemeUseCase(repository)

        useCase(AppTheme.DARK)

        coVerify { repository.updateAppTheme(AppTheme.DARK) }
    }

    // === NowPlayingTheme ===

    @Test
    fun `UpdateNowPlayingThemeUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateNowPlayingTheme(any()) }
        val useCase = UpdateNowPlayingThemeUseCase(repository)

        useCase(NowPlayingTheme.BLUR)

        coVerify { repository.updateNowPlayingTheme(NowPlayingTheme.BLUR) }
    }

    // === AMOLED Black ===

    @Test
    fun `UpdateUseAmoledBlackUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateUseAmoledBlack(any()) }
        val useCase = UpdateUseAmoledBlackUseCase(repository)

        useCase(true)

        coVerify { repository.updateUseAmoledBlack(true) }
    }

    // === Accent Color ===

    @Test
    fun `UpdateAccentColorUseCase delegates with value`() = runTest {
        coJustRun { repository.updateAccentColor(any()) }
        val useCase = UpdateAccentColorUseCase(repository)

        useCase(0xFF5722)

        coVerify { repository.updateAccentColor(0xFF5722) }
    }

    @Test
    fun `UpdateAccentColorUseCase delegates with null`() = runTest {
        coJustRun { repository.updateAccentColor(any()) }
        val useCase = UpdateAccentColorUseCase(repository)

        useCase(null)

        coVerify { repository.updateAccentColor(null) }
    }

    // === Mini Player Extra Controls ===

    @Test
    fun `UpdateShowMiniPlayerExtraControlsUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateShowMiniPlayerExtraControls(any()) }
        val useCase = UpdateShowMiniPlayerExtraControlsUseCase(repository)

        useCase(false)

        coVerify { repository.updateShowMiniPlayerExtraControls(false) }
    }
}
