package com.lotusreichhart.audily.core.mediastore.datasource

import android.content.ContentResolver
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaStoreDataSourceTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var dataSource: MediaStoreDataSource
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        contentResolver = mockk(relaxed = true)
        dataSource = MediaStoreDataSource(contentResolver, testDispatcher)
    }

    @Test
    fun `getSongs should register observer and emit values`() = runTest(testDispatcher) {
        // Mock query to return an empty list initially
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns null

        val songsFlow = dataSource.getSongs()
        val result = songsFlow.first()

        assertEquals(0, result.size)
        
        // Verify observer registration
        verify { contentResolver.registerContentObserver(any(), true, any()) }
    }

    @Test
    fun `getSongs with search and sort should call query with correct parameters`() = runTest(testDispatcher) {
        val searchQuery = "Hello"
        val sortOrder = MediaStoreSortOrder.ARTIST_DESC
        
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns null

        val songsFlow = dataSource.getSongs(searchQuery, sortOrder)
        songsFlow.first()

        verify {
            contentResolver.query(
                any(),
                any(),
                match { it.contains("title LIKE ?", ignoreCase = true) && it.contains("artist LIKE ?", ignoreCase = true) },
                match { it.contains("%Hello%") },
                eq(sortOrder.sqlOrder)
            )
        }
    }
}
