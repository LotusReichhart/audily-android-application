package com.lotusreichhart.audily.core.datastore

import androidx.datastore.core.DataStore
import com.lotusreichhart.audily.core.datastore.preferences.LibraryPreferences
import com.lotusreichhart.audily.core.datastore.preferences.PlaybackPreferences
import com.lotusreichhart.audily.core.datastore.preferences.UiPreferences
import com.lotusreichhart.audily.core.datastore.preferences.LyricsNetworkPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Điểm truy cập trung tâm cho toàn bộ DataStore.
 */
class AudilyDataStore @Inject constructor(
    dataStore: DataStore<UserPreferencesProto>,
    val library: LibraryPreferences,
    val ui: UiPreferences,
    val playback: PlaybackPreferences,
    val lyricsNetwork: LyricsNetworkPreferences
) {
    /**
     * Flow phản ứng (reactive) của toàn bộ UserPreferences.
     * Bất kỳ thay đổi nào từ bất kỳ Preferences con nào cũng sẽ được phát ra tự động.
     */
    val userPreferences: Flow<UserPreferencesProto> = dataStore.data
}
