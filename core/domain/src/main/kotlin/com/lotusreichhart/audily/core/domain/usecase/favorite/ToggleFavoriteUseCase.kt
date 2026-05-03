package com.lotusreichhart.audily.core.domain.usecase.favorite

import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import javax.inject.Inject

/**
 * Đảo ngược trạng thái yêu thích của một bài hát.
 * Nếu bài hát đã yêu thích -> Xóa khỏi yêu thích.
 * Nếu chưa yêu thích -> Thêm vào yêu thích.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(songId: Long) {
        favoritesRepository.toggleFavorite(songId)
    }
}