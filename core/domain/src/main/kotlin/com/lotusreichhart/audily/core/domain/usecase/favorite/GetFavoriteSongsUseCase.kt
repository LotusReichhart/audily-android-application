package com.lotusreichhart.audily.core.domain.usecase.favorite

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteSongsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<PagingData<Song>> = favoritesRepository.getFavoriteSongsPaged()
}