package com.lotusreichhart.audily.core.data.repository.playlist

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.lotusreichhart.audily.core.data.mapper.playlist.toDaoSortOrderType
import com.lotusreichhart.audily.core.data.mapper.playlist.toPlaylist
import com.lotusreichhart.audily.core.data.mapper.playlist.toPlaylistDaoSortOrder
import com.lotusreichhart.audily.core.data.mapper.song.toSong
import com.lotusreichhart.audily.core.data.paging.AudilyPagingConfig
import com.lotusreichhart.audily.core.database.dao.PlaylistDao
import com.lotusreichhart.audily.core.database.entity.PlaylistEntity
import com.lotusreichhart.audily.core.database.entity.PlaylistSongCrossRef
import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val mediaStoreDataSource: MediaStoreDataSource,
) : PlaylistRepository {

    override fun getPlaylists(
        searchQuery: String,
        sortOrder: PlaylistSortOrder,
        sortType: SortOrderType
    ): Flow<List<Playlist>> {
        return playlistDao.getPlaylists(
            searchQuery = searchQuery,
            sortOrder = sortOrder.toPlaylistDaoSortOrder(),
            sortType = sortType.toDaoSortOrderType()
        ).map { entities ->
            entities.map { withCount ->
                withCount.playlist.toPlaylist(songCount = withCount.songCount)
            }
        }
    }

    override fun getPlaylistById(id: Long): Flow<Playlist?> {
        return combine(
            playlistDao.getPlaylistById(id),
            playlistDao.getSongIdsInPlaylist(id)
        ) { entity, ids ->
            entity?.toPlaylist(songCount = ids.size)
        }
    }

    override suspend fun createPlaylist(name: String, description: String?): Long {
        val playlist = PlaylistEntity(
            name = name,
            imageUri = null,
            createdAt = System.currentTimeMillis()
        )
        return playlistDao.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(id: Long) {
        playlistDao.deletePlaylist(id)
    }

    override suspend fun addSongToPlaylist(id: Long, songId: Long) {
        val maxPos = playlistDao.getMaxPositionInPlaylist(id) ?: -1
        playlistDao.upsertSongToPlaylist(
            PlaylistSongCrossRef(
                playlistId = id,
                songId = songId,
                addedAt = System.currentTimeMillis(),
                position = maxPos + 1
            )
        )
    }

    override suspend fun removeSongFromPlaylist(id: Long, songId: Long) {
        playlistDao.deleteSongFromPlaylist(id, songId)
    }

    override fun getSongIdsInPlaylist(id: Long): Flow<List<Long>> {
        return playlistDao.getSongIdsInPlaylist(id)
    }

    override fun getPlaylistSongsPaged(id: Long): Flow<PagingData<Song>> {
        return Pager(
            config = AudilyPagingConfig.defaultConfig(enablePlaceholders = true),
            pagingSourceFactory = {
                playlistDao.getPlaylistSongsPaging(id)
            }
        ).flow.map { pagingData ->
            pagingData.map { crossRef ->
                mediaStoreDataSource.getSong(crossRef.songId)?.toSong(position = crossRef.position)
                    ?: Song(
                        id = crossRef.songId,
                        basic = BasicSongMetadata.EMPTY,
                        isMissing = true,
                        position = crossRef.position
                    )
            }
        }
    }

    override suspend fun updateSongPositionsInPlaylist(id: Long, songIds: List<Long>) {
        val time = System.currentTimeMillis()
        val crossRefs = songIds.mapIndexed { index, songId ->
            PlaylistSongCrossRef(
                playlistId = id,
                songId = songId,
                addedAt = time,
                position = index
            )
        }
        playlistDao.upsertSongsToPlaylist(crossRefs)
    }
}