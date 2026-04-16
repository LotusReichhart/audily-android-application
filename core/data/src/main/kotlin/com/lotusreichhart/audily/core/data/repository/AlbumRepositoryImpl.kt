package com.lotusreichhart.audily.core.data.repository

import com.lotusreichhart.audily.core.domain.repository.AlbumRepository
import com.lotusreichhart.audily.core.model.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(

) : AlbumRepository {
    override fun getAlbums(): Flow<List<Album>> {
        TODO("Not yet implemented")
    }

    override fun getAlbum(id: Long): Flow<Album> {
        TODO("Not yet implemented")
    }
}