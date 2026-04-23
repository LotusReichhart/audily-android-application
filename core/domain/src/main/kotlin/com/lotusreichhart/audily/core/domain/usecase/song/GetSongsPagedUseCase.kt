package com.lotusreichhart.audily.core.domain.usecase.song

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsPagedUseCase @Inject constructor(
    private val songRepository: SongRepository,
) {
    operator fun invoke(
        searchQuery: String? = null,
        sortOrder: SongSortOrder = SongSortOrder.TITLE_ASC
    ): Flow<PagingData<Song>> = songRepository.getSongsPaged(searchQuery, sortOrder)
}