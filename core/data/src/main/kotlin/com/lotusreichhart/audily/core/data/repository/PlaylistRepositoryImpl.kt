package com.lotusreichhart.audily.core.data.repository

import com.lotusreichhart.audily.core.domain.repository.PlaylistRepository
import com.lotusreichhart.audily.core.model.Playlist
import com.lotusreichhart.audily.core.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(

) : PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> {
        TODO("Not yet implemented")
    }

    override fun getPlaylistById(id: Long): Flow<Playlist?> {
        TODO("Not yet implemented")
    }

    override suspend fun createPlaylist(
        name: String,
        description: String?
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun addSongToPlaylist(id: Long, songId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun removeSongFromPlaylist(id: Long, songId: Long) {
        TODO("Not yet implemented")
    }

    override fun getSongsInPlaylist(id: Long): Flow<List<Song>> {
        TODO("Not yet implemented")
    }
}