package com.lotusreichhart.audily.core.data.mapper.prefs

import com.lotusreichhart.audily.core.datastore.UserPreferencesProto
import com.lotusreichhart.audily.core.model.prefs.UserPreferences

internal fun UserPreferencesProto.toDomain(): UserPreferences {
    return UserPreferences(
        librarySettings = librarySettings.toDomain(),
        uiSettings = uiSettings.toDomain(),
        playbackSettings = playbackSettings.toDomain(),
        lyricsNetworkSettings = lyricsNetworkSettings.toDomain()
    )
}
