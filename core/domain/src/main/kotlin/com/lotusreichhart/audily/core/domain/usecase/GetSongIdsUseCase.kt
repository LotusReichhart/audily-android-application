package com.lotusreichhart.audily.core.domain.usecase

import com.lotusreichhart.audily.core.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongIdsUseCase @Inject constructor(
    private val songRepository: SongRepository,
) {
    operator fun invoke(): Flow<List<Long>> = songRepository.getSongIds()
}
