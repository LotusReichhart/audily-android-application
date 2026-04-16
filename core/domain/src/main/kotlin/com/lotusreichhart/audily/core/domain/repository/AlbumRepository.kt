package com.lotusreichhart.audily.core.domain.repository

import com.lotusreichhart.audily.core.model.album.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    /**
     * Lấy danh sách Album (Basic info)
     */
    fun getAlbums(): Flow<List<Album>>

    /**
     * Lấy thông tin chi tiết của một Album (bao gồm Metadata)
     */
    fun getAlbum(id: Long): Flow<Album?>
}