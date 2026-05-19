package com.lotusreichhart.audily.core.domain.usecase.favorite

import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase lấy danh sách tóm tắt các bài hát yêu thích (dùng cho Preview/Artwork).
 */
class GetFavoriteSongsSummaryUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(limit: Int = 4): Flow<List<Song>> =
        favoritesRepository.getFavoriteSongsSummary(limit)
}
