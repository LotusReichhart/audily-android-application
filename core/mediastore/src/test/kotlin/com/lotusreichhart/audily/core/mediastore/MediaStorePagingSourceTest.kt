package com.lotusreichhart.audily.core.mediastore

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import androidx.paging.PagingSource
import com.lotusreichhart.audily.core.mediastore.model.BasicMediaStoreMetadata
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MediaStorePagingSourceTest {

    private lateinit var contentResolver: ContentResolver
    private val mockUri = mockk<Uri>(relaxed = true)
    
    @Before
    fun setup() {
        contentResolver = mockk(relaxed = true)
    }

    @Test
    fun `load should return Page when query is successful`() = runTest {
        val pagingSource = MediaStorePagingSource(
            contentResolver = contentResolver,
            musicUri = mockUri
        )

        val mockSongs = List(10) { id ->
            MediaStoreSong(
                id = id.toLong(),
                basic = BasicMediaStoreMetadata(
                    title = "Song $id",
                    artist = "Artist $id",
                    album = "Album $id",
                    duration = 3000L,
                    path = "/path/$id",
                    albumId = 1L,
                    dateModified = 1000L
                ),
                extended = null
            )
        }

        // Mock query call inside queryBasicSongs
        val mockCursor = mockk<Cursor>(relaxed = true) {
            every { moveToNext() } returnsMany List(10) { true } + false
            every { getLong(any()) } returns 1L
            every { getString(any()) } returns "Mock Data"
        }
        
        every { 
            contentResolver.query(eq(mockUri), any(), any(), any(), any()) 
        } returns mockCursor

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(10, page.data.size)
        assertEquals(null, page.prevKey)
        assertEquals(10, page.nextKey)
    }

    @Test
    fun `load should return Error when query fails`() = runTest {
        val pagingSource = MediaStorePagingSource(
            contentResolver = contentResolver,
            musicUri = mockUri
        )

        every { 
            contentResolver.query(any(), any(), any(), any(), any()) 
        } throws RuntimeException("Database error")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }
}
