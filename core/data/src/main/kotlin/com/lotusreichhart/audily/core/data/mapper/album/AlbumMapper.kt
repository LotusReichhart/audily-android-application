package com.lotusreichhart.audily.core.data.mapper.album

import com.lotusreichhart.audily.core.mediastore.model.MediaStoreAlbum
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreAlbumSortMetadata
import com.lotusreichhart.audily.core.model.album.Album

internal fun MediaStoreAlbumSortMetadata.toAlbum(): Album {
    return Album(
        id = id,
        title = title,
        artist = artist,
        albumArtUri = albumArtUri ?: "",
        songCount = songCount
    )
}

internal fun MediaStoreAlbum.toAlbum(): Album {
    return Album(
        id = id,
        title = title,
        artist = artist,
        albumArtUri = albumArtUri,
        songCount = songCount
    )
}
