package com.lotusreichhart.audily.core.playback.mapper

import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.Song
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MappersTest {

    @Test
    fun `MediaItemMapper should convert Song to MediaItem correctly`() {
        val song = Song(
            id = 123L,
            basic = BasicSongMetadata(
                title = "Song Title",
                artist = "Artist Name",
                album = "Album Name",
                albumId = 1L,
                duration = 5000L,
                path = "file:///path/to/song.mp3"
            )
        )

        val mediaItem = MediaItemMapper.toMediaItem(song)

        assertEquals("123", mediaItem.mediaId)
        assertEquals("file:///path/to/song.mp3", mediaItem.localConfiguration?.uri?.toString())
    }
}
