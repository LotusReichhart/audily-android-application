package com.lotusreichhart.audily.feature.settings.impl.lyricsnetwork

import com.lotusreichhart.audily.core.model.prefs.LyricsProvider

internal sealed interface LyricsNetworkUiEvent {
    data class OnPreferEmbeddedOfflineLyricsChanged(val prefer: Boolean) : LyricsNetworkUiEvent
    data class OnDefaultLyricsSourceChanged(val source: LyricsProvider) : LyricsNetworkUiEvent
    data class OnDownloadHighResAlbumArtWifiOnlyChanged(val wifiOnly: Boolean) : LyricsNetworkUiEvent
    data class OnFetchMissingArtistImagesChanged(val fetch: Boolean) : LyricsNetworkUiEvent
}