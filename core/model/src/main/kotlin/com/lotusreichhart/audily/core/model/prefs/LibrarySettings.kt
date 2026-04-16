package com.lotusreichhart.audily.core.model.prefs

/**
 * Các thiết lập liên quan đến thư viện nhạc (Persistent).
 */
data class LibrarySettings(
    val excludedFolders: List<String> = emptyList(),
    val minAudioDuration: Long = 30_000, // Bỏ qua file < 30s
    val filterSmallFiles: Boolean = true,
    val albumGridSize: Int = 2
)
