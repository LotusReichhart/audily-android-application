package com.lotusreichhart.audily.core.domain.usecase.favorite

import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(id: Long) = favoritesRepository.toggleFavorite(id)
}