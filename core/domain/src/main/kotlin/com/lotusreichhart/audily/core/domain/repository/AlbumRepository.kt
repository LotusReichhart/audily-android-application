package com.lotusreichhart.audily.core.domain.repository

import com.lotusreichhart.audily.core.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbums(): Flow<List<Album>>
    fun getAlbum(id: Long): Flow<Album?>
}