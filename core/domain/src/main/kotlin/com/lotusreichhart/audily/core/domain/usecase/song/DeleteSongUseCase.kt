package com.lotusreichhart.audily.core.domain.usecase.song

import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.song.DeleteSongResult
import javax.inject.Inject

/**
 * UseCase để xóa bài hát khỏi thiết bị.
 */
class DeleteSongUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(songId: Long): DeleteSongResult {
        return songRepository.deleteSong(songId)
    }
}
