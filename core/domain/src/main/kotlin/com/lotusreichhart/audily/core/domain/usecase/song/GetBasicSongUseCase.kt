package com.lotusreichhart.audily.core.domain.usecase.song

import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase lấy thông tin cơ bản của một bài hát theo ID.
 */
class GetBasicSongUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(id: Long): Flow<Song?> = songRepository.getBasicSong(id)
}
