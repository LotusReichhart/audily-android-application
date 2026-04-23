package com.lotusreichhart.audily.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lotusreichhart.audily.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class LocalDatabaseTest {

    private lateinit var db: AudilyDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AudilyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun verify_database_initialization_with_refined_schema() = runTest {
        val favoritesDao = db.favoritesDao()
        val favorite = FavoriteEntity(songId = 99L, createdAt = System.currentTimeMillis())
        favoritesDao.upsertFavorite(favorite)
        
        val result = favoritesDao.getFavoriteIds().first()
        assertEquals(listOf(99L), result)
    }
}
