package com.lotusreichhart.audily.feature.songs.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.songs.api.navigation.SongsNavKey
import com.lotusreichhart.audily.feature.songs.impl.SongsScreen

fun EntryProviderScope<NavKey>.songsEntry(
    navigator: Navigator
) {
    entry<SongsNavKey> {
        SongsScreen()
    }
}