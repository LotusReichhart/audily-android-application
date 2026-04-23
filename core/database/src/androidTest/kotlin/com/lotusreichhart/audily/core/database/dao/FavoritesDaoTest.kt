package com.lotusreichhart.audily.core.database.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lotusreichhart.audily.core.database.AudilyDatabase
import com.lotusreichhart.audily.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FavoritesDaoTest {

    private lateinit var favoritesDao: FavoritesDao
    private lateinit var db: AudilyDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AudilyDatabase::class.java).build()
        favoritesDao = db.favoritesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun upsertFavorite_and_getFavoriteIds() = runTest {
        val favorite = FavoriteEntity(songId = 1L, createdAt = 1000L, position = 0)
        favoritesDao.upsertFavorite(favorite)
        
        val favoriteIds = favoritesDao.getFavoriteIds().first()
        assertEquals(listOf(1L), favoriteIds)
    }

    @Test
    fun deleteFavorite_removes_from_list() = runTest {
        val favorite = FavoriteEntity(songId = 1L, createdAt = 1000L, position = 0)
        favoritesDao.upsertFavorite(favorite)
        favoritesDao.deleteFavorite(1L)
        
        val favoriteIds = favoritesDao.getFavoriteIds().first()
        assertTrue(favoriteIds.isEmpty())
    }

    @Test
    fun getFavoriteIds_is_sorted_by_position() = runTest {
        favoritesDao.upsertFavorite(FavoriteEntity(songId = 1L, createdAt = 1000L, position = 1))
        favoritesDao.upsertFavorite(FavoriteEntity(songId = 2L, createdAt = 2000L, position = 0))
        
        val favoriteIds = favoritesDao.getFavoriteIds().first()
        assertEquals(listOf(2L, 1L), favoriteIds)
    }

    @Test
    fun getFavoriteEntitiesPaging_returns_correct_paged_data() = runTest {
        favoritesDao.upsertFavorite(FavoriteEntity(songId = 10L, createdAt = 1000L, position = 2))
        favoritesDao.upsertFavorite(FavoriteEntity(songId = 20L, createdAt = 2000L, position = 1))
        favoritesDao.upsertFavorite(FavoriteEntity(songId = 30L, createdAt = 3000L, position = 0))

        val pagingSource = favoritesDao.getFavoriteEntitiesPaging()
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(30L, page.data[0].songId)
        assertEquals(20L, page.data[1].songId)
        assertEquals(10L, page.data[2].songId)
    }
}
