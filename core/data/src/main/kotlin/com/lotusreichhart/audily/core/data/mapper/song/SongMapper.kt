package com.lotusreichhart.audily.core.data.mapper.song

import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSongsSummary
import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.ExtendedSongMetadata
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongsSummary

internal fun MediaStoreSong.toSong(position: Int? = null): Song {
    return Song(
        id = this.id,
        position = position,
        basic = BasicSongMetadata(
            title = this.basic.title,
            artist = this.basic.artist,
            album = this.basic.album,
            albumId = this.basic.albumId,
            duration = this.basic.duration,
            path = this.basic.path,
            artworkUri = this.basic.artworkUri
        ),
        extended = this.extended?.let { extended ->
            ExtendedSongMetadata(
                trackNumber = extended.track,
                year = extended.year,
                fileSize = extended.size,
                composer = extended.composer,
                addedAt = this.basic.dateModified
            )
        }
    )
}
