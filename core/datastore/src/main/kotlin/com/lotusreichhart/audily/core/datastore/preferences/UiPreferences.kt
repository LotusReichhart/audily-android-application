package com.lotusreichhart.audily.core.datastore.preferences

import androidx.datastore.core.DataStore
import com.lotusreichhart.audily.core.datastore.AppThemeProto
import com.lotusreichhart.audily.core.datastore.NowPlayingThemeProto
import com.lotusreichhart.audily.core.datastore.UserPreferencesProto
import com.lotusreichhart.audily.core.datastore.AppLanguageProto
import javax.inject.Inject

/**
 * Quản lý các thiết lập liên quan đến giao diện người dùng.
 */
class UiPreferences @Inject constructor(
    private val dataStore: DataStore<UserPreferencesProto>
) {
    suspend fun updateAppTheme(theme: AppThemeProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setUiSettings(
                    prefs.uiSettings.toBuilder()
                        .setAppTheme(theme)
                )
                .build()
        }
    }

    suspend fun updateNowPlayingTheme(theme: NowPlayingThemeProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setUiSettings(
                    prefs.uiSettings.toBuilder()
                        .setNowPlayingTheme(theme)
                )
                .build()
        }
    }

    suspend fun updateUseAmoledBlack(enabled: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setUiSettings(
                    prefs.uiSettings.toBuilder()
                        .setUseAmoledBlack(enabled)
                )
                .build()
        }
    }

    suspend fun updateAccentColor(color: Int?) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setUiSettings(
                    prefs.uiSettings.toBuilder()
                        .setHasAccentColor(color != null)
                        .setAccentColor(color ?: 0)
                )
                .build()
        }
    }

    suspend fun updateShowMiniPlayerExtraControls(show: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setUiSettings(
                    prefs.uiSettings.toBuilder()
                        .setShowMiniPlayerExtraControls(show)
                )
                .build()
        }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setUiSettings(
                    prefs.uiSettings.toBuilder()
                        .setDynamicColor(enabled)
                )
                .build()
        }
    }

    suspend fun updateUseGlassmorphism(enabled: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setUiSettings(
                    prefs.uiSettings.toBuilder()
                        .setHasUseGlassmorphism(true)
                        .setUseGlassmorphism(enabled)
                )
                .build()
        }
    }

    suspend fun updateAppLanguage(language: AppLanguageProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setUiSettings(
                    prefs.uiSettings.toBuilder()
                        .setAppLanguage(language)
                )
                .build()
        }
    }
}