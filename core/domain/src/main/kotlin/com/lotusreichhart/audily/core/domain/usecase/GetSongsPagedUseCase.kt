package com.lotusreichhart.audily.core.domain.usecase

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.domain.repository.SongRepository
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsPagedUseCase @Inject constructor(
    private val songRepository: SongRepository,
) {
    operator fun invoke(): Flow<PagingData<Song>> = songRepository.getSongsPaged()
}
