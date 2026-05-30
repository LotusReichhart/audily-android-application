package com.lotusreichhart.audily.core.domain.usecase.album

import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetAlbumSongsUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(albumId: Long): Flow<List<Song>> {
        return songRepository.getSongIdsByAlbum(albumId).flatMapLatest { ids ->
            songRepository.getSongsByIds(ids)
        }
    }
}
