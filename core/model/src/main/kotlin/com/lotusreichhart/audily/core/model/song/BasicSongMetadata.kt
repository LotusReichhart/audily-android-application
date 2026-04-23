package com.lotusreichhart.audily.core.model.song

data class BasicSongMetadata(
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val path: String
) {
    companion object {
        val EMPTY = BasicSongMetadata(
            title = "",
            artist = "",
            album = "",
            albumId = -1L,
            duration = 0L,
            path = ""
        )
    }
}
