package com.lotusreichhart.audily.core.domain.usecase.favorite

import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteIds @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<Long>> = favoritesRepository.getFavoriteIds()
}