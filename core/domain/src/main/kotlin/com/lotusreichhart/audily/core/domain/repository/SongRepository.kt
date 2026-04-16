package com.lotusreichhart.audily.core.domain.repository

import com.lotusreichhart.audily.core.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getSongs(): Flow<List<Song>>
    fun getSong(id: Long): Flow<Song?>
}