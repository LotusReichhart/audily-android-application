package com.lotusreichhart.audily.feature.songs.impl.menu

sealed class SongMenuAction {
    data class Play(val songId: Long, val queueIds: List<Long>) : SongMenuAction()
    data class ResumePause(val isPlaying: Boolean) : SongMenuAction()
    data class ToggleFavorite(val isFavorite: Boolean) : SongMenuAction()

    object PlayOnce : SongMenuAction()
    object PlayNext : SongMenuAction()
    object AddToQueue : SongMenuAction()
    object AddToPlaylist : SongMenuAction()
    data class RemoveFromPlaylist(val playlistId: Long? = null, val songId: Long) : SongMenuAction()
    object ShowInfo : SongMenuAction()
    object EditTags : SongMenuAction()
    object SetRingtone : SongMenuAction()
    object Share : SongMenuAction()
    object Delete : SongMenuAction()
}