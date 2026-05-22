package com.lotusreichhart.audily.core.domain.usecase.history

import com.lotusreichhart.audily.core.domain.repository.history.HistoryRepository
import javax.inject.Inject

/**
 * UseCase để xóa lịch sử nghe nhạc của một bài hát cụ thể.
 */
class DeleteHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(songId: Long) {
        historyRepository.deleteHistory(songId)
    }
}
