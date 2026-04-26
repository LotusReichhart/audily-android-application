package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Tests cho tất cả Library Settings UseCases.
 */
class UpdateLibrarySettingsUseCaseTest {

    private lateinit var repository: UserPreferencesRepository

    @Before
    fun setup() {
        repository = mockk()
    }

    // === Excluded Folders ===

    @Test
    fun `UpdateExcludedFoldersUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateExcludedFolders(any()) }
        val useCase = UpdateExcludedFoldersUseCase(repository)

        val folders = listOf("/storage/Ringtones", "/storage/Notifications")
        useCase(folders)

        coVerify { repository.updateExcludedFolders(folders) }
    }

    @Test
    fun `UpdateExcludedFoldersUseCase delegates with empty list`() = runTest {
        coJustRun { repository.updateExcludedFolders(any()) }
        val useCase = UpdateExcludedFoldersUseCase(repository)

        useCase(emptyList())

        coVerify { repository.updateExcludedFolders(emptyList()) }
    }

    // === Min Audio Duration ===

    @Test
    fun `UpdateMinAudioDurationUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateMinAudioDuration(any()) }
        val useCase = UpdateMinAudioDurationUseCase(repository)

        useCase(60_000L)

        coVerify { repository.updateMinAudioDuration(60_000L) }
    }

    // === Filter Small Files ===

    @Test
    fun `UpdateFilterSmallFilesUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateFilterSmallFiles(any()) }
        val useCase = UpdateFilterSmallFilesUseCase(repository)

        useCase(false)

        coVerify { repository.updateFilterSmallFiles(false) }
    }

    // === Album Grid Size ===

    @Test
    fun `UpdateAlbumGridSizeUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateAlbumGridSize(any()) }
        val useCase = UpdateAlbumGridSizeUseCase(repository)

        useCase(3)

        coVerify { repository.updateAlbumGridSize(3) }
    }
}
