package com.lotusreichhart.audily.core.data.repository.favorite

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.lotusreichhart.audily.core.data.mapper.song.toSong
import com.lotusreichhart.audily.core.data.paging.AudilyPagingConfig
import com.lotusreichhart.audily.core.database.dao.FavoritesDao
import com.lotusreichhart.audily.core.database.entity.FavoriteEntity
import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val mediaStoreDataSource: MediaStoreDataSource
) : FavoritesRepository {

    override fun isFavorite(id: Long): Flow<Boolean> {
        return favoritesDao.isFavorite(id)
    }

    override suspend fun toggleFavorite(id: Long) {
        val isFavorite = favoritesDao.isFavorite(id).first()
        if (isFavorite) {
            favoritesDao.deleteFavorite(id)
        } else {
            val maxPos = favoritesDao.getMaxPosition() ?: -1
            favoritesDao.upsertFavorite(
                FavoriteEntity(
                    songId = id,
                    createdAt = System.currentTimeMillis(),
                    position = maxPos + 1
                )
            )
        }
    }

    override fun getFavoriteIds(): Flow<List<Long>> {
        return favoritesDao.getFavoriteIds()
    }

    override fun getFavoriteSongsPaged(): Flow<PagingData<Song>> {
        return Pager(
            config = AudilyPagingConfig.defaultConfig(enablePlaceholders = true),
            pagingSourceFactory = {
                favoritesDao.getFavoriteEntitiesPaging()
            }
        ).flow.map { pagingData ->
            pagingData.map { favorite ->
                mediaStoreDataSource.getSong(favorite.songId)
                    ?.toSong(position = favorite.position)
                    ?.copy(isFavorite = true)
                    ?: Song(
                        id = favorite.songId,
                        basic = BasicSongMetadata.EMPTY,
                        isFavorite = true,
                        isMissing = true,
                        position = favorite.position
                    )
            }
        }
    }

    override fun getFavoriteSongsSummary(limit: Int): Flow<List<Song>> {
        return favoritesDao.getFavoriteEntities(limit).map { entities ->
            entities.map { favorite ->
                mediaStoreDataSource.getSong(favorite.songId)
                    ?.toSong(position = favorite.position)
                    ?.copy(isFavorite = true)
                    ?: Song(
                        id = favorite.songId,
                        basic = BasicSongMetadata.EMPTY,
                        isFavorite = true,
                        isMissing = true,
                        position = favorite.position
                    )
            }
        }
    }

    override suspend fun updateFavoritePositions(songIds: List<Long>) {
        val time = System.currentTimeMillis()
        val favorites = songIds.mapIndexed { index, id ->
            FavoriteEntity(
                songId = id,
                createdAt = time,
                position = index
            )
        }
        favoritesDao.upsertFavorites(favorites)
    }
}