package com.lotusreichhart.audily.feature.songs.impl.menu

import com.lotusreichhart.audily.core.ui.GlobalSheetKey
import com.lotusreichhart.audily.core.ui.GlobalSheetRegistry

object SongMenuInitializer {
    fun initialize() {
        GlobalSheetRegistry.register(GlobalSheetKey.SONG_MENU) { params ->
            SongMenuSheet(params = params)
        }
    }
}
