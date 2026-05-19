package com.lotusreichhart.audily.core.domain.usecase.favorite

import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import javax.inject.Inject

/**
 * UseCase xóa toàn bộ danh sách bài hát yêu thích.
 */
class ClearFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke() {
        favoritesRepository.clearFavorites()
    }
}
