package com.lotusreichhart.audily.core.data.repository.favorite

import com.lotusreichhart.audily.core.database.dao.FavoritesDao
import com.lotusreichhart.audily.core.database.entity.FavoriteEntity
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FavoritesRepositoryImplTest {

    private lateinit var favoritesDao: FavoritesDao
    private lateinit var mediaStoreDataSource: MediaStoreDataSource
    private lateinit var repository: FavoritesRepositoryImpl

    @Before
    fun setup() {
        favoritesDao = mockk()
        mediaStoreDataSource = mockk()
        repository = FavoritesRepositoryImpl(favoritesDao, mediaStoreDataSource)
    }

    @Test
    fun `isFavorite returns correct flow from dao`() = runTest {
        val songId = 1L
        every { favoritesDao.isFavorite(songId) } returns flowOf(true)

        repository.isFavorite(songId).collect {
            assertEquals(true, it)
        }
    }

    @Test
    fun `toggleFavorite deletes favorite when already exists`() = runTest {
        val songId = 1L
        every { favoritesDao.isFavorite(songId) } returns flowOf(true)
        coEvery { favoritesDao.deleteFavorite(songId) } returns Unit

        repository.toggleFavorite(songId)

        coVerify { favoritesDao.deleteFavorite(songId) }
    }

    @Test
    fun `toggleFavorite inserts favorite with correct position when not exists`() = runTest {
        val songId = 1L
        every { favoritesDao.isFavorite(songId) } returns flowOf(false)
        coEvery { favoritesDao.getMaxPosition() } returns 5
        coEvery { favoritesDao.upsertFavorite(any()) } returns Unit

        repository.toggleFavorite(songId)

        coVerify {
            favoritesDao.upsertFavorite(match { 
                it.songId == songId && it.position == 6 
            })
        }
    }
}
