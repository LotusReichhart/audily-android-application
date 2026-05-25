package com.lotusreichhart.audily.core.domain.usecase.history

import com.lotusreichhart.audily.core.domain.repository.history.HistoryRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.StopQueueUseCase
import javax.inject.Inject

/**
 * UseCase xóa toàn bộ lịch sử nghe nhạc, hàng đợi, phiên phát nhạc hiện tại
 * và dừng hoàn toàn nhạc đang phát.
 */
class ClearAllHistoryUseCase @Inject constructor(
    private val stopQueueUseCase: StopQueueUseCase,
    private val historyRepository: HistoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        // 1. Stop ExoPlayer & clear session
        stopQueueUseCase()
        // 2. Clear playing queue in DB
        userPreferencesRepository.clearPlayingQueue()
        // 3. Clear listening history in DB
        historyRepository.clearHistory()
    }
}
