package com.lotusreichhart.audily.feature.songs.impl.menu

sealed interface SongMenuUiEffect {
    data class AddSongToPlaylists(val songId: Long) : SongMenuUiEffect
}