package com.lotusreichhart.audily.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lotusreichhart.audily.core.database.AudilyDatabase
import com.lotusreichhart.audily.core.database.entity.PlaylistEntity
import com.lotusreichhart.audily.core.database.entity.PlaylistSongCrossRef
import com.lotusreichhart.audily.core.database.model.DaoSortOrderType
import com.lotusreichhart.audily.core.database.model.PlaylistDaoSortOrder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PlaylistDaoTest {

    private lateinit var playlistDao: PlaylistDao
    private lateinit var db: AudilyDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AudilyDatabase::class.java).build()
        playlistDao = db.playlistDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun playlist_crud_with_search() = runTest {
        val p1 = PlaylistEntity(name = "Rock Music", imageUri = null, createdAt = 1000L)
        val p2 = PlaylistEntity(name = "Pop Hits", imageUri = null, createdAt = 2000L)
        playlistDao.insertPlaylist(p1)
        playlistDao.insertPlaylist(p2)
        
        val searchRock = playlistDao.getPlaylists(
            searchQuery = "Rock", 
            sortOrder = PlaylistDaoSortOrder.NAME,
            sortType = DaoSortOrderType.ASC
        ).first()
        assertEquals(1, searchRock.size)
        assertEquals("Rock Music", searchRock[0].playlist.name)
    }

    @Test
    fun playlist_localized_sorting() = runTest {
        val p1 = PlaylistEntity(name = "Ân", imageUri = null, createdAt = 1000L)
        val p2 = PlaylistEntity(name = "Anh", imageUri = null, createdAt = 2000L)
        val p3 = PlaylistEntity(name = "Ăn", imageUri = null, createdAt = 3000L)
        
        playlistDao.insertPlaylist(p1)
        playlistDao.insertPlaylist(p2)
        playlistDao.insertPlaylist(p3)
        
        val sorted = playlistDao.getPlaylists(
            sortOrder = PlaylistDaoSortOrder.NAME,
            sortType = DaoSortOrderType.ASC
        ).first()
        assertEquals("Anh", sorted[0].playlist.name)
        assertEquals("Ăn", sorted[1].playlist.name)
        assertEquals("Ân", sorted[2].playlist.name)
    }

    @Test
    fun playlist_song_count_reactive() = runTest {
        val playlistId = playlistDao.insertPlaylist(
            PlaylistEntity(name = "Test", imageUri = null, createdAt = 1000L)
        )
        
        // Ban đầu count = 0
        val initial = playlistDao.getPlaylists(sortOrder = PlaylistDaoSortOrder.NAME, sortType = DaoSortOrderType.ASC).first()
        assertEquals(0, initial[0].songCount)
        
        // Thêm bài hát
        playlistDao.upsertSongToPlaylist(
            PlaylistSongCrossRef(playlistId = playlistId, songId = 1L, position = 0)
        )
        
        // Sau khi thêm count = 1
        val updated = playlistDao.getPlaylists(sortOrder = PlaylistDaoSortOrder.NAME, sortType = DaoSortOrderType.ASC).first()
        assertEquals(1, updated[0].songCount)
    }

    @Test
    fun playlist_song_reordering_by_position() = runTest {
        val playlistId = playlistDao.insertPlaylist(
            PlaylistEntity(name = "Test", imageUri = null, createdAt = 1000L)
        )
        
        playlistDao.upsertSongToPlaylist(
            PlaylistSongCrossRef(playlistId = playlistId, songId = 1L, position = 1)
        )
        playlistDao.upsertSongToPlaylist(
            PlaylistSongCrossRef(playlistId = playlistId, songId = 2L, position = 0)
        )
        
        val songIds = playlistDao.getSongIdsInPlaylist(playlistId).first()
        assertEquals(listOf(2L, 1L), songIds)
    }
}
