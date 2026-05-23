package com.lotusreichhart.audily.core.domain.usecase.history

import com.lotusreichhart.audily.core.domain.repository.history.HistoryRepository
import javax.inject.Inject

/**
 * UseCase xử lý việc cập nhật lịch sử khi người dùng nghe xong một bài hát.
 */
class UpdateHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(songId: Long) {
        historyRepository.upsertHistory(songId)
    }
}
