package com.lotusreichhart.audily.core.data.mapper.prefs

import com.lotusreichhart.audily.core.datastore.LibrarySettingsProto
import com.lotusreichhart.audily.core.model.prefs.LibrarySettings

internal fun LibrarySettingsProto.toDomain(): LibrarySettings {
    return LibrarySettings(
        excludedFolders = excludedFoldersList,
        minAudioDuration = if (minAudioDuration > 0) minAudioDuration else 30_000L,
        filterSmallFiles = filterSmallFiles,
        albumGridSize = if (albumGridSize > 0) albumGridSize else 2
    )
}
