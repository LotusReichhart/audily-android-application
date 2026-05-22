package com.lotusreichhart.audily.feature.edittag.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.edittag.api.navigation.EditTagNavKey
import com.lotusreichhart.audily.feature.edittag.impl.EditTagScreen

fun EntryProviderScope<NavKey>.editTagEntry(
    navigator: Navigator
) {
    entry<EditTagNavKey> { key ->
        EditTagScreen(
            songId = key.songId,
            onBack = {
                navigator.goBack()
            }
        )
    }
}