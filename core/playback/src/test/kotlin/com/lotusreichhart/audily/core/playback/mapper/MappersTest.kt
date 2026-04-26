package com.lotusreichhart.audily.core.playback.mapper

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.Song
import io.mockk.every
import io.mockk.mockk
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

    @Test
    fun `PlaybackStateMapper should map Player state correctly`() {
        val player = mockk<Player>(relaxed = true)
        val mediaItem = MediaItem.Builder()
            .setMediaId("456")
            .setUri("file:///test.mp3")
            .build()
        
        every { player.playbackState } returns Player.STATE_READY
        every { player.isPlaying } returns true
        every { player.currentMediaItem } returns mediaItem
        every { player.currentPosition } returns 1000L
        every { player.bufferedPosition } returns 2000L
        every { player.duration } returns 3000L
        every { player.shuffleModeEnabled } returns true
        every { player.repeatMode } returns Player.REPEAT_MODE_ONE
        every { player.mediaItemCount } returns 2
        
        val item1 = MediaItem.Builder().setMediaId("10").setUri("f1").build()
        val item2 = MediaItem.Builder().setMediaId("20").setUri("f2").build()
        every { player.getMediaItemAt(0) } returns item1
        every { player.getMediaItemAt(1) } returns item2

        val state = PlaybackStateMapper.map(player)

        assertEquals(NowPlayingState.PLAYING, state.nowPlayingState)
        assertEquals(456L, state.currentSongId)
        assertEquals(1000L, state.playbackPosition)
        assertEquals(2000L, state.bufferedPosition)
        assertEquals(3000L, state.duration)
        assertEquals(true, state.isShuffleOn)
        assertEquals(RepeatMode.ONE, state.repeatMode)
        assertEquals(listOf(10L, 20L), state.queueIds)
    }
}
