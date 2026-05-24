package com.lotusreichhart.audily.core.model.prefs

/**
 * Các thiết lập liên quan đến giao diện người dùng (Persistent).
 */
data class UiSettings(
    val appTheme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val nowPlayingTheme: NowPlayingTheme = NowPlayingTheme.DEFAULT,
    val useAmoledBlack: Boolean = false,
    val accentColor: Int? = null,
    val showMiniPlayerExtraControls: Boolean = true,
    val dynamicColor: Boolean = false,
    val useGlassmorphism: Boolean = true
)
