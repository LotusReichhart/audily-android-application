package com.lotusreichhart.audily.core.datastore.preferences

import androidx.datastore.core.DataStore
import com.lotusreichhart.audily.core.datastore.UserPreferencesProto
import javax.inject.Inject

/**
 * Quản lý các thiết lập liên quan đến thư viện nhạc.
 */
class LibraryPreferences @Inject constructor(
    private val dataStore: DataStore<UserPreferencesProto>
) {
    suspend fun updateExcludedFolders(folders: List<String>) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(
                    prefs.librarySettings.toBuilder()
                        .clearExcludedFolders()
                        .addAllExcludedFolders(folders)
                )
                .build()
        }
    }

    suspend fun updateMinAudioDuration(duration: Long) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(
                    prefs.librarySettings.toBuilder()
                        .setMinAudioDuration(duration)
                )
                .build()
        }
    }

    suspend fun updateFilterSmallFiles(enabled: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(
                    prefs.librarySettings.toBuilder()
                        .setFilterSmallFiles(enabled)
                )
                .build()
        }
    }

    suspend fun updateAlbumGridSize(size: Int) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(
                    prefs.librarySettings.toBuilder()
                        .setAlbumGridSize(size)
                )
                .build()
        }
    }
}