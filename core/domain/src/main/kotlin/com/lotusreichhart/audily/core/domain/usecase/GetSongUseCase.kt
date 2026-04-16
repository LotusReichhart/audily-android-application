package com.lotusreichhart.audily.core.domain.usecase

import com.lotusreichhart.audily.core.domain.repository.SongRepository
import com.lotusreichhart.audily.core.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongUseCase @Inject constructor(
    private val songRepository: SongRepository,
) {
    operator fun invoke(id: Long): Flow<Song?> = songRepository.getSong(id)
}