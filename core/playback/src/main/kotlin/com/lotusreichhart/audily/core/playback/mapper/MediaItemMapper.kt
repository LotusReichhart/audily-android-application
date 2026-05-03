package com.lotusreichhart.audily.core.playback.mapper

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.lotusreichhart.audily.core.model.song.Song
import androidx.core.net.toUri

/**
 * Chuyển đổi giữa Model bài hát của Audily và MediaItem của Media3.
 */
object MediaItemMapper {
    fun toMediaItem(song: Song): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(song.basic.title)
            .setArtist(song.basic.artist)
            .setArtworkUri(song.basic.artworkUri?.toUri())
            .build()

        return MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.basic.path)
            .setMediaMetadata(metadata)
            .build()
    }

    fun toMediaItems(songs: List<Song>): List<MediaItem> {
        return songs.map { toMediaItem(it) }
    }
}
