package com.lotusreichhart.audily.feature.songs.impl.menu

import com.lotusreichhart.audily.core.model.song.Song

data class SongMenuUiState(
    val song: Song,
    val caller: String = "",
    val options: List<SongMenuAction> = emptyList()
)

