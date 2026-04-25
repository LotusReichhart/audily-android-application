package com.lotusreichhart.audily.navigation

import androidx.annotation.StringRes
import com.lotusreichhart.audily.R
import com.lotusreichhart.audily.feature.home.api.R as homeApiR
import com.lotusreichhart.audily.feature.focus.api.R as focusApiR
import com.lotusreichhart.audily.feature.settings.api.R as settingsApiR
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.feature.focus.api.navigation.FocusNavKey
import com.lotusreichhart.audily.feature.home.api.navigation.HomeNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.SettingsNavKey

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

val TOP_LEVEL_NAV_ITEMS = mapOf(
    HomeNavKey to HOME,
    FocusNavKey to FOCUS,
    SettingsNavKey to SETTINGS,
)
