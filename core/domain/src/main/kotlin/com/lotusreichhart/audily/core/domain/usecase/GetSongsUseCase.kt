package com.lotusreichhart.audily.core.domain.usecase

import com.lotusreichhart.audily.core.model.Song
import com.lotusreichhart.audily.core.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val songRepository: SongRepository,
) {
    operator fun invoke(): Flow<List<Song>> = songRepository.getSongs()
}