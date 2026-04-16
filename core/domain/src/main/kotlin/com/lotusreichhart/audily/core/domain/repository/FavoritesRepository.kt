package com.lotusreichhart.audily.core.domain.repository

import com.lotusreichhart.audily.core.model.Song
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun isFavorite(id: Long): Flow<Boolean>
    suspend fun toggleFavorite(id: Long)
    fun getFavorites(): Flow<List<Song>>
}