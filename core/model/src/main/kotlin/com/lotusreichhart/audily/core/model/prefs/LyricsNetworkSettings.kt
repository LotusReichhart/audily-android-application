package com.lotusreichhart.audily.core.model.prefs

/**
 * Các thiết lập liên quan đến lời bài hát và mạng.
 */
data class LyricsNetworkSettings(
    val preferEmbeddedOfflineLyrics: Boolean = true,
    val defaultLyricsSource: LyricsProvider = LyricsProvider.LRCLIB,
    val downloadHighResAlbumArtWifiOnly: Boolean = false,
    val fetchMissingArtistImages: Boolean = true
)
