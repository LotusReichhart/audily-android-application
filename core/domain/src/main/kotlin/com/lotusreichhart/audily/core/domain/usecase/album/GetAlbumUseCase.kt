package com.lotusreichhart.audily.core.domain.usecase.album

import com.lotusreichhart.audily.core.domain.repository.album.AlbumRepository
import com.lotusreichhart.audily.core.model.album.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(id: Long): Flow<Album?> = albumRepository.getAlbum(id)
}