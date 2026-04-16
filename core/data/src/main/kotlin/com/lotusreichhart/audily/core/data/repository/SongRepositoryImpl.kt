package com.lotusreichhart.audily.core.data.repository

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.domain.repository.SongRepository
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(

) : SongRepository {
    override fun getSongIds(): Flow<List<Long>> {
        TODO("Not yet implemented")
    }

    override fun getSongsPaged(): Flow<PagingData<Song>> {
        TODO("Not yet implemented")
    }

    override fun getSong(id: Long): Flow<Song?> {
        TODO("Not yet implemented")
    }

}