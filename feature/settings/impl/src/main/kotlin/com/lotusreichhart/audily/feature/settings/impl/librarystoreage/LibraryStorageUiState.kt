package com.lotusreichhart.audily.feature.settings.impl.librarystoreage

internal data class LibraryStorageUiState(
    val excludedFolders: List<String> = emptyList(),
    val isScanning: Boolean = false
)
