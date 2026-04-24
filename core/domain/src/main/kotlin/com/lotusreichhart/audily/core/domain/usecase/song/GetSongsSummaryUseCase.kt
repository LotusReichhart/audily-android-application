package com.lotusreichhart.audily.core.domain.usecase.song

import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase lấy thông tin tóm tắt của danh sách bài hát (số lượng, tổng thời lượng).
 */
class GetSongsSummaryUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(searchQuery: String? = null): Flow<SongsSummary> {
        return songRepository.getSongsSummary(searchQuery)
    }
}
