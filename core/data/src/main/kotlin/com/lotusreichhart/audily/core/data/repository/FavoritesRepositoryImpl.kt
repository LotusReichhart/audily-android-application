package com.lotusreichhart.audily.core.data.repository

import com.lotusreichhart.audily.core.domain.repository.FavoritesRepository
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(

) : FavoritesRepository {
    override fun isFavorite(id: Long): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleFavorite(id: Long) {
        TODO("Not yet implemented")
    }

    override fun getFavorites(): Flow<List<Song>> {
        TODO("Not yet implemented")
    }
}