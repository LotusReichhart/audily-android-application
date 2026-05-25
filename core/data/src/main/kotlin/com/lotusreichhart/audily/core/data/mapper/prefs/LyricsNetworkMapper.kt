package com.lotusreichhart.audily.core.data.mapper.prefs

import com.lotusreichhart.audily.core.datastore.LyricsNetworkSettingsProto
import com.lotusreichhart.audily.core.datastore.LyricsProviderProto
import com.lotusreichhart.audily.core.model.prefs.LyricsNetworkSettings
import com.lotusreichhart.audily.core.model.prefs.LyricsProvider

internal fun LyricsNetworkSettingsProto.toDomain(): LyricsNetworkSettings {
    return LyricsNetworkSettings(
        preferEmbeddedOfflineLyrics = preferEmbeddedOfflineLyrics,
        defaultLyricsSource = defaultLyricsSource.toDomain(),
        downloadHighResAlbumArtWifiOnly = downloadHighResAlbumArtWifiOnly,
        fetchMissingArtistImages = fetchMissingArtistImages
    )
}

internal fun LyricsProviderProto.toDomain(): LyricsProvider {
    return when (this) {
        LyricsProviderProto.LYRICS_PROVIDER_LRCLIB -> LyricsProvider.LRCLIB
        LyricsProviderProto.UNRECOGNIZED -> LyricsProvider.LRCLIB
    }
}

internal fun LyricsProvider.toProto(): LyricsProviderProto {
    return when (this) {
        LyricsProvider.LRCLIB -> LyricsProviderProto.LYRICS_PROVIDER_LRCLIB
    }
}
