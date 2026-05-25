package com.lotusreichhart.audily.core.datastore.preferences

import androidx.datastore.core.DataStore
import com.lotusreichhart.audily.core.datastore.LyricsNetworkSettingsProto
import com.lotusreichhart.audily.core.datastore.LyricsProviderProto
import com.lotusreichhart.audily.core.datastore.UserPreferencesProto
import javax.inject.Inject

/**
 * Quản lý các thiết lập liên quan đến lời bài hát và mạng.
 */
class LyricsNetworkPreferences @Inject constructor(
    private val dataStore: DataStore<UserPreferencesProto>
) {
    suspend fun updatePreferEmbeddedOfflineLyrics(prefer: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLyricsNetworkSettings(
                    prefs.lyricsNetworkSettings.toBuilder()
                        .setPreferEmbeddedOfflineLyrics(prefer)
                )
                .build()
        }
    }

    suspend fun updateDefaultLyricsSource(source: LyricsProviderProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLyricsNetworkSettings(
                    prefs.lyricsNetworkSettings.toBuilder()
                        .setDefaultLyricsSource(source)
                )
                .build()
        }
    }

    suspend fun updateDownloadHighResAlbumArtWifiOnly(wifiOnly: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLyricsNetworkSettings(
                    prefs.lyricsNetworkSettings.toBuilder()
                        .setDownloadHighResAlbumArtWifiOnly(wifiOnly)
                )
                .build()
        }
    }

    suspend fun updateFetchMissingArtistImages(fetch: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLyricsNetworkSettings(
                    prefs.lyricsNetworkSettings.toBuilder()
                        .setFetchMissingArtistImages(fetch)
                )
                .build()
        }
    }
}
