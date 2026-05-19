package com.lotusreichhart.audily.feature.favorites.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.favorites.api.navigation.FavoritesNavKey
import com.lotusreichhart.audily.feature.favorites.impl.FavoritesScreen

fun EntryProviderScope<NavKey>.favoritesEntry(
    navigator: Navigator
) {
    entry<FavoritesNavKey> {
        FavoritesScreen(onBack = { navigator.goBack() })
    }
}