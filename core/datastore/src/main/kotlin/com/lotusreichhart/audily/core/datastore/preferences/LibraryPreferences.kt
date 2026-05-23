package com.lotusreichhart.audily.core.datastore.preferences

import androidx.datastore.core.DataStore
import com.lotusreichhart.audily.core.datastore.UserPreferencesProto
import com.lotusreichhart.audily.core.datastore.SongSortOrderProto
import com.lotusreichhart.audily.core.datastore.SortOrderTypeProto
import com.lotusreichhart.audily.core.datastore.AlbumSortOrderProto
import com.lotusreichhart.audily.core.datastore.PlaylistSortOrderProto
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

    // === Sort Preferences ===

    suspend fun updateSongSortOrder(order: SongSortOrderProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(prefs.librarySettings.toBuilder().setSongSortOrder(order))
                .build()
        }
    }

    suspend fun updateSongSortType(type: SortOrderTypeProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(prefs.librarySettings.toBuilder().setSongSortType(type))
                .build()
        }
    }

    suspend fun updateAlbumSortOrder(order: AlbumSortOrderProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(prefs.librarySettings.toBuilder().setAlbumSortOrder(order))
                .build()
        }
    }

    suspend fun updateAlbumSortType(type: SortOrderTypeProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(prefs.librarySettings.toBuilder().setAlbumSortType(type))
                .build()
        }
    }

    suspend fun updatePlaylistSortOrder(order: PlaylistSortOrderProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(prefs.librarySettings.toBuilder().setPlaylistSortOrder(order))
                .build()
        }
    }

    suspend fun updatePlaylistSortType(type: SortOrderTypeProto) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLibrarySettings(prefs.librarySettings.toBuilder().setPlaylistSortType(type))
                .build()
        }
    }
}