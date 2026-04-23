package com.lotusreichhart.audily.core.domain.usecase.album

import com.lotusreichhart.audily.core.domain.repository.album.AlbumRepository
import com.lotusreichhart.audily.core.model.album.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Flow<List<Album>> = albumRepository.getAlbums()
}