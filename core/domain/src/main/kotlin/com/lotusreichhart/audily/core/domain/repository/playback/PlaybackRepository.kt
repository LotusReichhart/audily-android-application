package com.lotusreichhart.audily.core.domain.repository.playback

import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playback.SleepTimerStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlaybackRepository {
    val playbackState: StateFlow<PlaybackState>
    val sleepTimerStatus: StateFlow<SleepTimerStatus>

    /**
     * Điểm nhận lệnh tập trung cho trình phát nhạc.
     */
    suspend fun handleEvent(event: PlaybackEvent)

    /**
     * Theo dõi vị trí phát nhạc hiện tại (ms).
     */
    fun observePlaybackPosition(): Flow<Long>

    /**
     * Kiểm tra trình phát có vừa được tái tạo và cần khôi phục dữ liệu không.
     */
    fun needsRestoration(): Boolean

    /**
     * Đánh dấu trình phát đã sẵn sàng (đã khôi phục session xong).
     */
    fun markAsInitialized()

    /**
     * Bật/tắt trạng thái đang khôi phục dữ liệu.
     */
    fun setRestoring(restoring: Boolean)

    /**
     * Thiết lập bộ hẹn giờ tắt nhạc. Truyền 0 để hủy.
     */
    fun setSleepTimer(durationMs: Long)
}