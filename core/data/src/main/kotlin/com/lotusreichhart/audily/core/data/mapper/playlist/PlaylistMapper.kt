package com.lotusreichhart.audily.core.data.mapper.playlist

import com.lotusreichhart.audily.core.database.entity.PlaylistEntity
import com.lotusreichhart.audily.core.model.playlist.Playlist

internal fun PlaylistEntity.toPlaylist(
    songCount: Int = 0,
    artworkUris: List<String?> = emptyList()
): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        songCount = songCount,
        artworkUris = artworkUris,
        createdAt = this.createdAt
    )
}
