package com.lotusreichhart.audily.core.playback.mapper

import androidx.media3.common.MediaItem
import com.lotusreichhart.audily.core.model.song.Song

/**
 * Chuyển đổi giữa Model bài hát của Audily và MediaItem của Media3.
 */
object MediaItemMapper {
    fun toMediaItem(song: Song): MediaItem {
        return MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.basic.path)
            .build()
    }

    fun toMediaItems(songs: List<Song>): List<MediaItem> {
        return songs.map { toMediaItem(it) }
    }
}
