package com.lotusreichhart.audily.core.data.repository.playlist

import com.lotusreichhart.audily.core.database.dao.PlaylistDao
import com.lotusreichhart.audily.core.database.model.PlaylistDaoSortOrder
import com.lotusreichhart.audily.core.database.model.PlaylistWithCount
import com.lotusreichhart.audily.core.database.entity.PlaylistEntity
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlaylistRepositoryImplTest {

    private lateinit var playlistDao: PlaylistDao
    private lateinit var mediaStoreDataSource: MediaStoreDataSource
    private lateinit var repository: PlaylistRepositoryImpl

    @Before
    fun setup() {
        playlistDao = mockk()
        mediaStoreDataSource = mockk()
        repository = PlaylistRepositoryImpl(playlistDao, mediaStoreDataSource)
    }

    @Test
    fun `getPlaylists returns mapped domain objects`() = runTest {
        val entity = PlaylistEntity(id = 1, name = "Test", imageUri = null, createdAt = 0)
        val withCount = PlaylistWithCount(entity, 5)

        every { 
            playlistDao.getPlaylists(any(), PlaylistDaoSortOrder.NAME_ASC) 
        } returns flowOf(listOf(withCount))

        val result = repository.getPlaylists("", PlaylistSortOrder.NAME_ASC).first()

        assertEquals(1, result.size)
        assertEquals("Test", result[0].name)
        assertEquals(5, result[0].songCount)
    }

    @Test
    fun `createPlaylist calls dao insert`() = runTest {
        coEvery { playlistDao.insertPlaylist(any()) } returns 1L

        val id = repository.createPlaylist("New Playlist")

        assertEquals(1L, id)
        coVerify { playlistDao.insertPlaylist(match { it.name == "New Playlist" }) }
    }

    @Test
    fun `addSongToPlaylist inserts crossRef with correct position`() = runTest {
        coEvery { playlistDao.getMaxPositionInPlaylist(1L) } returns 10
        coEvery { playlistDao.upsertSongToPlaylist(any()) } returns Unit

        repository.addSongToPlaylist(1L, 100L)

        coVerify {
            playlistDao.upsertSongToPlaylist(match { 
                it.playlistId == 1L && it.songId == 100L && it.position == 11 
            })
        }
    }

    @Test
    fun `updateSongPositionsInPlaylist calls upsertSongsToPlaylist`() = runTest {
        val songIds = listOf(10L, 20L)
        coEvery { playlistDao.upsertSongsToPlaylist(any()) } returns Unit

        repository.updateSongPositionsInPlaylist(1L, songIds)

        coVerify {
            playlistDao.upsertSongsToPlaylist(match { list ->
                list.size == 2 && 
                list[0].songId == 10L && list[0].position == 0 &&
                list[1].songId == 20L && list[1].position == 1
            })
        }
    }
}
