package com.lotusreichhart.audily.core.mediastore

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
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
    private val mockMusicUri = mockk<Uri>(relaxed = true)
    private val mockAlbumUri = mockk<Uri>(relaxed = true)

    @Before
    fun setup() {
        contentResolver = mockk(relaxed = true)
        dataSource = MediaStoreDataSource(
            contentResolver = contentResolver,
            ioDispatcher = testDispatcher,
            musicUri = mockMusicUri,
            albumsUri = mockAlbumUri
        )
    }

    @Test
    fun `getSong should call querySongById`() {
        val songId = 123L
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns null

        dataSource.getSong(songId)

        verify {
            contentResolver.query(
                eq(mockMusicUri),
                any(),
                eq("${MediaStore.Audio.Media._ID} = ?"),
                match { it[0] == songId.toString() },
                null
            )
        }
    }
}