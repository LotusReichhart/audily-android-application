package com.lotusreichhart.audily.core.data.repository

import com.lotusreichhart.audily.core.domain.repository.SongRepository
import com.lotusreichhart.audily.core.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(

) : SongRepository {
    override fun getSongs(): Flow<List<Song>> {
        TODO("Not yet implemented")
    }

    override fun getSong(id: Long): Flow<Song?> {
        TODO("Not yet implemented")
    }
}