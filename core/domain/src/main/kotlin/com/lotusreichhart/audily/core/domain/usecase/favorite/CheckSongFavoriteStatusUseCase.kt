package com.lotusreichhart.audily.core.domain.usecase.favorite

import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckSongFavoriteStatusUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(songId: Long): Flow<Boolean> = favoritesRepository.isFavorite(songId)
}