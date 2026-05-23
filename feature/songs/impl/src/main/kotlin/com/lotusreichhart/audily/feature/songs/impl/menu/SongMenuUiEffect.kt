package com.lotusreichhart.audily.feature.songs.impl.menu

import com.lotusreichhart.audily.core.model.song.Song

sealed interface SongMenuUiEffect {
    data class AddSongToPlaylists(val songId: Long) : SongMenuUiEffect
    data class EditTag(val songId: Long) : SongMenuUiEffect
    data class ShareSong(val song: Song) : SongMenuUiEffect
}