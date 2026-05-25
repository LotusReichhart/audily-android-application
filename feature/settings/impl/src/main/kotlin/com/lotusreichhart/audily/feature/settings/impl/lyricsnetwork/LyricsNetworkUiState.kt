package com.lotusreichhart.audily.feature.settings.impl.lyricsnetwork

import com.lotusreichhart.audily.core.model.prefs.LyricsProvider

internal data class LyricsNetworkUiState(
    val isLoading: Boolean = true,
    val preferEmbeddedOfflineLyrics: Boolean = true,
    val defaultLyricsSource: LyricsProvider = LyricsProvider.LRCLIB,
    val downloadHighResAlbumArtWifiOnly: Boolean = false,
    val fetchMissingArtistImages: Boolean = true
)
