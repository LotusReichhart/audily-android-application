package com.lotusreichhart.audily.core.mediastore

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSortOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaStoreDataSourceTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var dataSource: MediaStoreDataSource
    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockUri = mockk<Uri>(relaxed = true)

    @Before
    fun setup() {
        contentResolver = mockk(relaxed = true)
        dataSource = MediaStoreDataSource(
            contentResolver = contentResolver,
            ioDispatcher = testDispatcher,
            musicUri = mockUri
        )
    }

    @Test
    fun `getSongIds should register observer and emit values`() = runTest(testDispatcher) {
        // Mock query to return an empty list initially
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns null

        val idsFlow = dataSource.getSongIds()
        val result = idsFlow.first()

        Assert.assertEquals(0, result.size)

        // Verify observer registration
        verify { contentResolver.registerContentObserver(eq(mockUri), true, any()) }
    }

    @Test
    fun `getSongIds with search and sort should call query with correct parameters`() =
        runTest(testDispatcher) {
            val searchQuery = "Hello"
            val sortOrder = MediaStoreSortOrder.ARTIST_DESC

            every { contentResolver.query(any(), any(), any(), any(), any()) } returns null

            val idsFlow = dataSource.getSongIds(searchQuery, sortOrder)
            idsFlow.first()

            verify {
                contentResolver.query(
                    eq(mockUri),
                    any(),
                    match {
                        it.contains(
                            "title LIKE ?",
                            ignoreCase = true
                        ) && it.contains("artist LIKE ?", ignoreCase = true)
                    },
                    match { it.any { arg -> arg == "%Hello%" } },
                    eq(sortOrder.sqlOrder)
                )
            }
        }

    @Test
    fun `getSong should call querySongById`() {
        val songId = 123L
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns null

        dataSource.getSong(songId)

        verify {
            contentResolver.query(
                eq(mockUri),
                any(),
                eq("${MediaStore.Audio.Media._ID} = ?"),
                match { it[0] == songId.toString() },
                null
            )
        }
    }
}