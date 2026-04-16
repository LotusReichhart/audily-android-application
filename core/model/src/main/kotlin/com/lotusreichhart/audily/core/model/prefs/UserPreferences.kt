package com.lotusreichhart.audily.core.model.prefs

/**
 * Model tổng hợp cho tất cả tùy chọn của người dùng (Persistent).
 */
data class UserPreferences(
    val librarySettings: LibrarySettings = LibrarySettings(),
    val uiSettings: UiSettings = UiSettings(),
    val playerSettings: PlayerSettings = PlayerSettings()
)
