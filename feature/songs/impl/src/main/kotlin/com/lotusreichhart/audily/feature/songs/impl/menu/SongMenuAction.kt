package com.lotusreichhart.audily.feature.songs.impl.menu

sealed class SongMenuAction {
    data class Play(val songId: Long, val queueIds: List<Long>) : SongMenuAction()
    data class PlayOnce(val songId: Long) : SongMenuAction()

    data class ResumePause(val isPlaying: Boolean) : SongMenuAction()
    object PlayNext : SongMenuAction()
    object AddToQueue : SongMenuAction()
    object AddToPlaylist : SongMenuAction()
    data class ToggleFavorite(val isFavorite: Boolean) : SongMenuAction()
    object EditTags : SongMenuAction()
    object Share : SongMenuAction()
    object Delete : SongMenuAction()
}