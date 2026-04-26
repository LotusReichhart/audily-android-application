package com.lotusreichhart.audily.core.data.mapper.prefs

import com.lotusreichhart.audily.core.datastore.AppThemeProto
import com.lotusreichhart.audily.core.datastore.NowPlayingThemeProto
import com.lotusreichhart.audily.core.datastore.UiSettingsProto
import com.lotusreichhart.audily.core.model.prefs.AppTheme
import com.lotusreichhart.audily.core.model.prefs.NowPlayingTheme
import com.lotusreichhart.audily.core.model.prefs.UiSettings

internal fun UiSettingsProto.toDomain(): UiSettings {
    return UiSettings(
        appTheme = appTheme.toDomain(),
        nowPlayingTheme = nowPlayingTheme.toDomain(),
        useAmoledBlack = useAmoledBlack,
        accentColor = if (hasAccentColor) accentColor else null,
        showMiniPlayerExtraControls = showMiniPlayerExtraControls
    )
}

internal fun AppThemeProto.toDomain(): AppTheme = when (this) {
    AppThemeProto.APP_THEME_LIGHT -> AppTheme.LIGHT
    AppThemeProto.APP_THEME_DARK -> AppTheme.DARK
    AppThemeProto.APP_THEME_DYNAMIC -> AppTheme.DYNAMIC
    else -> AppTheme.FOLLOW_SYSTEM
}

internal fun AppTheme.toProto(): AppThemeProto = when (this) {
    AppTheme.LIGHT -> AppThemeProto.APP_THEME_LIGHT
    AppTheme.DARK -> AppThemeProto.APP_THEME_DARK
    AppTheme.DYNAMIC -> AppThemeProto.APP_THEME_DYNAMIC
    AppTheme.FOLLOW_SYSTEM -> AppThemeProto.APP_THEME_FOLLOW_SYSTEM
}

internal fun NowPlayingThemeProto.toDomain(): NowPlayingTheme = when (this) {
    NowPlayingThemeProto.NOW_PLAYING_THEME_BLUR -> NowPlayingTheme.BLUR
    NowPlayingThemeProto.NOW_PLAYING_THEME_MINIMAL -> NowPlayingTheme.MINIMAL
    NowPlayingThemeProto.NOW_PLAYING_THEME_FULL_SCREEN_ART -> NowPlayingTheme.FULL_SCREEN_ART
    else -> NowPlayingTheme.DEFAULT
}

internal fun NowPlayingTheme.toProto(): NowPlayingThemeProto = when (this) {
    NowPlayingTheme.DEFAULT -> NowPlayingThemeProto.NOW_PLAYING_THEME_DEFAULT
    NowPlayingTheme.BLUR -> NowPlayingThemeProto.NOW_PLAYING_THEME_BLUR
    NowPlayingTheme.MINIMAL -> NowPlayingThemeProto.NOW_PLAYING_THEME_MINIMAL
    NowPlayingTheme.FULL_SCREEN_ART -> NowPlayingThemeProto.NOW_PLAYING_THEME_FULL_SCREEN_ART
}
