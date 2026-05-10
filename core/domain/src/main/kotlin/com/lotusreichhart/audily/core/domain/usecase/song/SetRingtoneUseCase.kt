package com.lotusreichhart.audily.core.domain.usecase.song

import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.song.RingtoneResult
import javax.inject.Inject

/**
 * UseCase để cài đặt bài hát làm nhạc chuông hệ thống.
 */
class SetRingtoneUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(songId: Long): RingtoneResult {
        return songRepository.setAsRingtone(songId)
    }
}
