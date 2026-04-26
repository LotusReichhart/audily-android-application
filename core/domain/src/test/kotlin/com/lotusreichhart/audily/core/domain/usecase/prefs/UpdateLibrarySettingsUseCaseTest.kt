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

    // === Sort Preferences ===

    @Test
    fun `UpdateSongSortOrderUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateSongSortOrder(any()) }
        val useCase = UpdateSongSortOrderUseCase(repository)
        useCase(com.lotusreichhart.audily.core.model.song.SongSortOrder.TITLE)
        coVerify { repository.updateSongSortOrder(com.lotusreichhart.audily.core.model.song.SongSortOrder.TITLE) }
    }

    @Test
    fun `UpdateSongSortTypeUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateSongSortType(any()) }
        val useCase = UpdateSongSortTypeUseCase(repository)
        useCase(com.lotusreichhart.audily.core.model.common.SortOrderType.ASC)
        coVerify { repository.updateSongSortType(com.lotusreichhart.audily.core.model.common.SortOrderType.ASC) }
    }

    @Test
    fun `UpdateAlbumSortOrderUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateAlbumSortOrder(any()) }
        val useCase = UpdateAlbumSortOrderUseCase(repository)
        useCase(com.lotusreichhart.audily.core.model.album.AlbumSortOrder.ARTIST)
        coVerify { repository.updateAlbumSortOrder(com.lotusreichhart.audily.core.model.album.AlbumSortOrder.ARTIST) }
    }

    @Test
    fun `UpdateAlbumSortTypeUseCase delegates correctly`() = runTest {
        coJustRun { repository.updateAlbumSortType(any()) }
        val useCase = UpdateAlbumSortTypeUseCase(repository)
        useCase(com.lotusreichhart.audily.core.model.common.SortOrderType.DESC)
        coVerify { repository.updateAlbumSortType(com.lotusreichhart.audily.core.model.common.SortOrderType.DESC) }
    }

    @Test
    fun `UpdatePlaylistSortOrderUseCase delegates correctly`() = runTest {
        coJustRun { repository.updatePlaylistSortOrder(any()) }
        val useCase = UpdatePlaylistSortOrderUseCase(repository)
        useCase(com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder.NUMBER_OF_SONGS)
        coVerify { repository.updatePlaylistSortOrder(com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder.NUMBER_OF_SONGS) }
    }

    @Test
    fun `UpdatePlaylistSortTypeUseCase delegates correctly`() = runTest {
        coJustRun { repository.updatePlaylistSortType(any()) }
        val useCase = UpdatePlaylistSortTypeUseCase(repository)
        useCase(com.lotusreichhart.audily.core.model.common.SortOrderType.ASC)
        coVerify { repository.updatePlaylistSortType(com.lotusreichhart.audily.core.model.common.SortOrderType.ASC) }
    }
}
