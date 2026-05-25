package com.lotusreichhart.audily.feature.settings.impl.librarystoreage

internal sealed interface LibraryStorageUiEvent {
    data class AddExcludedFolder(val path: String) : LibraryStorageUiEvent
    data class RemoveExcludedFolder(val path: String) : LibraryStorageUiEvent
    data object RescanMediaStore : LibraryStorageUiEvent
    data object ClearAllHistory : LibraryStorageUiEvent
}