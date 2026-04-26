package com.lotusreichhart.audily.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.lotusreichhart.audily.core.database.entity.PlaylistEntity
import com.lotusreichhart.audily.core.database.entity.PlaylistSongCrossRef
import com.lotusreichhart.audily.core.database.model.DaoSortOrderType
import com.lotusreichhart.audily.core.database.model.PlaylistDaoSortOrder
import com.lotusreichhart.audily.core.database.model.PlaylistWithCount
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaylistDao {

    @RawQuery(observedEntities = [PlaylistEntity::class, PlaylistSongCrossRef::class])
    protected abstract fun getPlaylistsRaw(query: SupportSQLiteQuery): Flow<List<PlaylistWithCount>>

    fun getPlaylists(
        searchQuery: String = "",
        sortOrder: PlaylistDaoSortOrder = PlaylistDaoSortOrder.CREATED_DATE,
        sortType: DaoSortOrderType = DaoSortOrderType.DESC
    ): Flow<List<PlaylistWithCount>> {
        val order = sortType.sqlKey
        val column = sortOrder.column
        val queryStr = """
            SELECT *, (SELECT COUNT(*) FROM playlist_song_cross_ref WHERE playlist_id = playlists.id) as song_count 
            FROM playlists 
            WHERE name LIKE ? 
            ORDER BY $column $order
        """.trimIndent()
        return getPlaylistsRaw(SimpleSQLiteQuery(queryStr, arrayOf("%$searchQuery%")))
    }

    @Query("SELECT * FROM playlists WHERE id = :id")
    abstract fun getPlaylistById(id: Long): Flow<PlaylistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :id")
    abstract suspend fun deletePlaylist(id: Long)

    @Update
    abstract suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Upsert
    abstract suspend fun upsertSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Upsert
    abstract suspend fun upsertSongsToPlaylist(crossRefs: List<PlaylistSongCrossRef>)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlist_id = :playlistId AND song_id = :songId")
    abstract suspend fun deleteSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("SELECT song_id FROM playlist_song_cross_ref WHERE playlist_id = :playlistId ORDER BY position ASC")
    abstract fun getSongIdsInPlaylist(playlistId: Long): Flow<List<Long>>

    @Query("SELECT * FROM playlist_song_cross_ref WHERE playlist_id = :playlistId ORDER BY position ASC")
    abstract fun getPlaylistSongsPaging(playlistId: Long): PagingSource<Int, PlaylistSongCrossRef>

    @Query("SELECT MAX(position) FROM playlist_song_cross_ref WHERE playlist_id = :playlistId")
    abstract suspend fun getMaxPositionInPlaylist(playlistId: Long): Int?
}
