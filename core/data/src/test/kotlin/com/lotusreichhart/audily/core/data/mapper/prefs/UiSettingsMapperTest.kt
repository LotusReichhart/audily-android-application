package com.lotusreichhart.audily.core.data.mapper.prefs

import com.lotusreichhart.audily.core.datastore.AppThemeProto
import com.lotusreichhart.audily.core.datastore.NowPlayingThemeProto
import com.lotusreichhart.audily.core.model.prefs.AppTheme
import com.lotusreichhart.audily.core.model.prefs.NowPlayingTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UiSettingsMapperTest {

    // === AppTheme: Proto → Domain ===

    @Test
    fun `AppThemeProto LIGHT maps to AppTheme LIGHT`() {
        assertEquals(AppTheme.LIGHT, AppThemeProto.APP_THEME_LIGHT.toDomain())
    }

    @Test
    fun `AppThemeProto DARK maps to AppTheme DARK`() {
        assertEquals(AppTheme.DARK, AppThemeProto.APP_THEME_DARK.toDomain())
    }

    @Test
    fun `AppThemeProto DYNAMIC maps to AppTheme DYNAMIC`() {
        assertEquals(AppTheme.DYNAMIC, AppThemeProto.APP_THEME_DYNAMIC.toDomain())
    }

    @Test
    fun `AppThemeProto FOLLOW_SYSTEM maps to AppTheme FOLLOW_SYSTEM`() {
        assertEquals(AppTheme.FOLLOW_SYSTEM, AppThemeProto.APP_THEME_FOLLOW_SYSTEM.toDomain())
    }

    @Test
    fun `AppThemeProto UNRECOGNIZED maps to AppTheme FOLLOW_SYSTEM`() {
        assertEquals(AppTheme.FOLLOW_SYSTEM, AppThemeProto.UNRECOGNIZED.toDomain())
    }

    // === AppTheme: Domain → Proto ===

    @Test
    fun `AppTheme LIGHT maps to AppThemeProto LIGHT`() {
        assertEquals(AppThemeProto.APP_THEME_LIGHT, AppTheme.LIGHT.toProto())
    }

    @Test
    fun `AppTheme DARK maps to AppThemeProto DARK`() {
        assertEquals(AppThemeProto.APP_THEME_DARK, AppTheme.DARK.toProto())
    }

    @Test
    fun `AppTheme DYNAMIC maps to AppThemeProto DYNAMIC`() {
        assertEquals(AppThemeProto.APP_THEME_DYNAMIC, AppTheme.DYNAMIC.toProto())
    }

    @Test
    fun `AppTheme FOLLOW_SYSTEM maps to AppThemeProto FOLLOW_SYSTEM`() {
        assertEquals(AppThemeProto.APP_THEME_FOLLOW_SYSTEM, AppTheme.FOLLOW_SYSTEM.toProto())
    }

    // === NowPlayingTheme: Proto → Domain ===

    @Test
    fun `NowPlayingThemeProto DEFAULT maps to NowPlayingTheme DEFAULT`() {
        assertEquals(NowPlayingTheme.DEFAULT, NowPlayingThemeProto.NOW_PLAYING_THEME_DEFAULT.toDomain())
    }

    @Test
    fun `NowPlayingThemeProto BLUR maps to NowPlayingTheme BLUR`() {
        assertEquals(NowPlayingTheme.BLUR, NowPlayingThemeProto.NOW_PLAYING_THEME_BLUR.toDomain())
    }

    @Test
    fun `NowPlayingThemeProto MINIMAL maps to NowPlayingTheme MINIMAL`() {
        assertEquals(NowPlayingTheme.MINIMAL, NowPlayingThemeProto.NOW_PLAYING_THEME_MINIMAL.toDomain())
    }

    @Test
    fun `NowPlayingThemeProto FULL_SCREEN_ART maps to NowPlayingTheme FULL_SCREEN_ART`() {
        assertEquals(
            NowPlayingTheme.FULL_SCREEN_ART,
            NowPlayingThemeProto.NOW_PLAYING_THEME_FULL_SCREEN_ART.toDomain()
        )
    }

    @Test
    fun `NowPlayingThemeProto UNRECOGNIZED maps to NowPlayingTheme DEFAULT`() {
        assertEquals(NowPlayingTheme.DEFAULT, NowPlayingThemeProto.UNRECOGNIZED.toDomain())
    }

    // === NowPlayingTheme: Domain → Proto ===

    @Test
    fun `NowPlayingTheme DEFAULT maps to NowPlayingThemeProto DEFAULT`() {
        assertEquals(NowPlayingThemeProto.NOW_PLAYING_THEME_DEFAULT, NowPlayingTheme.DEFAULT.toProto())
    }

    @Test
    fun `NowPlayingTheme BLUR maps to NowPlayingThemeProto BLUR`() {
        assertEquals(NowPlayingThemeProto.NOW_PLAYING_THEME_BLUR, NowPlayingTheme.BLUR.toProto())
    }

    @Test
    fun `NowPlayingTheme MINIMAL maps to NowPlayingThemeProto MINIMAL`() {
        assertEquals(NowPlayingThemeProto.NOW_PLAYING_THEME_MINIMAL, NowPlayingTheme.MINIMAL.toProto())
    }

    @Test
    fun `NowPlayingTheme FULL_SCREEN_ART maps to NowPlayingThemeProto FULL_SCREEN_ART`() {
        assertEquals(
            NowPlayingThemeProto.NOW_PLAYING_THEME_FULL_SCREEN_ART,
            NowPlayingTheme.FULL_SCREEN_ART.toProto()
        )
    }
}
