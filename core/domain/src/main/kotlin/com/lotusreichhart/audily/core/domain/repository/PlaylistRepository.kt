package com.lotusreichhart.audily.core.domain.repository

import com.lotusreichhart.audily.core.model.Playlist
import com.lotusreichhart.audily.core.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Long): Flow<Playlist?>
    suspend fun createPlaylist(name: String, description: String?): Long
    suspend fun deletePlaylist(id: Long)
    suspend fun addSongToPlaylist(id: Long, songId: Long)
    suspend fun removeSongFromPlaylist(id: Long, songId: Long)
    fun getSongsInPlaylist(id: Long): Flow<List<Song>>
}