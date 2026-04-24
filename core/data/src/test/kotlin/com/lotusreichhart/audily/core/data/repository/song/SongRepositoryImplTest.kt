package com.lotusreichhart.audily.core.data.repository.song

import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.mediastore.model.BasicMediaStoreMetadata
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortMetadata
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

    private lateinit var mediaStoreDataSource: MediaStoreDataSource
    private lateinit var repository: SongRepositoryImpl

    @Before
    fun setup() {
        mediaStoreDataSource = mockk()
        repository = SongRepositoryImpl(mediaStoreDataSource)
    }

    @Test
    fun `getSongIds returns sorted ids from metadata`() = runTest {
        val metadata = listOf(
            MediaStoreSortMetadata(1L, "B Song", "Artist", 0L),
            MediaStoreSortMetadata(2L, "A Song", "Artist", 0L)
        )
        every { mediaStoreDataSource.getSongsSortMetadata(any()) } returns flowOf(metadata)

        val result = repository.getSongIds(null, SongSortOrder.TITLE_ASC).first()

        // Phải được sắp xếp lại bởi SongSorter trong Repository
        assertEquals(listOf(2L, 1L), result)
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
        every { mediaStoreDataSource.getSong(1L) } returns mediaStoreSong

        val result = repository.getSong(1L).first()

        assertEquals("Test Song", result?.basic?.title)
        assertEquals("Artist", result?.basic?.artist)
    }
}
