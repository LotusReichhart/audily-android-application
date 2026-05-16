package com.lotusreichhart.audily.navigation

import androidx.annotation.StringRes
import com.lotusreichhart.audily.R
import com.lotusreichhart.audily.feature.home.api.R as homeApiR
import com.lotusreichhart.audily.feature.focus.api.R as focusApiR
import com.lotusreichhart.audily.feature.settings.api.R as settingsApiR
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.feature.focus.api.navigation.FocusNavKey
import com.lotusreichhart.audily.feature.home.api.navigation.HomeNavKey
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistsNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.SettingsNavKey
import com.lotusreichhart.audily.feature.songs.api.navigation.SongsNavKey

/**
 * Định nghĩa thông tin hiển thị cho các mục điều hướng chính trên thanh Bottom Bar / Rail.
 */
data class TopLevelNavItem(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    @param:StringRes val iconTextId: Int,
    @param:StringRes val titleTextId: Int,
)

val HOME = TopLevelNavItem(
    selectedIcon = AudilyIcons.HomeFill,
    unselectedIcon = AudilyIcons.HomeOutline,
    iconTextId = homeApiR.string.feature_home_api_title,
    titleTextId = R.string.app_name
)

val FOCUS = TopLevelNavItem(
    selectedIcon = AudilyIcons.TimerFill,
    unselectedIcon = AudilyIcons.TimerOutline,
    iconTextId = focusApiR.string.feature_focus_api_title,
    titleTextId = focusApiR.string.feature_focus_api_title
)

val SETTINGS = TopLevelNavItem(
    selectedIcon = AudilyIcons.SettingsFill,
    unselectedIcon = AudilyIcons.SettingsOutline,
    iconTextId = settingsApiR.string.feature_settings_api_title,
    titleTextId = settingsApiR.string.feature_settings_api_title
)

/**
 * Danh sách các mục hiển thị trực tiếp trên thanh điều hướng (Bottom Bar / Navigation Rail).
 */
val NAV_BAR_ITEMS = mapOf(
    HomeNavKey to HOME,
    FocusNavKey to FOCUS,
    SettingsNavKey to SETTINGS,
)

/**
 * Tập hợp tất cả các Key được coi là Top Level trong kiến trúc ứng dụng.
 * Các Key này sẽ có BackStack riêng để tối ưu hiệu năng và giữ trạng thái UI.
 */
val TOP_LEVEL_NAV_ITEMS = NAV_BAR_ITEMS.keys + setOf(
    SongsNavKey,
    PlaylistsNavKey
    // PlaylistsNavKey, // Bổ sung sau
    // AlbumsNavKey,    // Bổ sung sau
)
