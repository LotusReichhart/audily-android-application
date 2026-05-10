package com.lotusreichhart.audily.feature.home.impl

import com.lotusreichhart.audily.core.model.song.Song

internal sealed interface HomeUiEvent {
    data class OnSongClick(val songId: Long, val contextSongs: List<Song>) : HomeUiEvent
    data object OnShuffleAll : HomeUiEvent
    data object OnResume : HomeUiEvent
}
