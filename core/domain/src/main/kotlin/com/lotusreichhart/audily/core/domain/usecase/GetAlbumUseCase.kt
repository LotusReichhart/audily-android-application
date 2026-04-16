package com.lotusreichhart.audily.core.domain.usecase

import com.lotusreichhart.audily.core.domain.repository.AlbumRepository
import com.lotusreichhart.audily.core.model.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(id: Long): Flow<Album?> = albumRepository.getAlbum(id)
}
