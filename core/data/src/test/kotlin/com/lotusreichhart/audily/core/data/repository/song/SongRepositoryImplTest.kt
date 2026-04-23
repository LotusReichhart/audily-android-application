package com.lotusreichhart.audily.core.data.repository.song

import android.content.ContentResolver
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.mediastore.model.BasicMediaStoreMetadata
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SongRepositoryImplTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var mediaStoreDataSource: MediaStoreDataSource
    private lateinit var repository: SongRepositoryImpl

    @Before
    fun setup() {
        contentResolver = mockk()
        mediaStoreDataSource = mockk()
        repository = SongRepositoryImpl(contentResolver, mediaStoreDataSource)
    }

    @Test
    fun `getSongIds returns ids from data source`() = runTest {
        val ids = listOf(1L, 2L)
        every { mediaStoreDataSource.getSongIds(any(), any()) } returns flowOf(ids)

        val result = repository.getSongIds(null, SongSortOrder.TITLE_ASC).first()

        assertEquals(ids, result)
    }

    @Test
    fun `getSong returns mapped song object`() = runTest {
        val mediaStoreSong = MediaStoreSong(
            id = 1L,
            basic = BasicMediaStoreMetadata(
                title = "Test Song",
                artist = "Artist",
                album = "Album",
                albumId = 1L,
                duration = 100,
                path = "path",
                dateModified = 0
            )
        )
        coEvery { mediaStoreDataSource.getSong(1L) } returns mediaStoreSong

        val result = repository.getSong(1L).first()

        assertEquals("Test Song", result?.basic?.title)
        assertEquals("Artist", result?.basic?.artist)
    }
}
