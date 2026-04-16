package com.lotusreichhart.audily.core.domain.usecase

import com.lotusreichhart.audily.core.domain.repository.FavoritesRepository
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteSongsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<Song>> = favoritesRepository.getFavorites()
}