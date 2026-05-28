package com.lotusreichhart.audily.core.domain.usecase.playback.state

import com.lotusreichhart.audily.core.domain.usecase.playback.palette.ObserveCurrentPaletteUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.ObserveQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingData
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import timber.log.Timber
import javax.inject.Inject

/**
 * UseCase gộp dữ liệu cho màn hình Now Playing.
 * Kết hợp thông tin từ PlaybackState, UserPreferences và trích xuất PaletteColors.
 */
class ObserveNowPlayingUseCase @Inject constructor(
    private val observePlaybackState: ObservePlaybackStateUseCase,
    private val observeQueueUseCase: ObserveQueueUseCase,
    private val observeCurrentPalette: ObserveCurrentPaletteUseCase,
    private val getPrefs: GetUserPreferencesUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<NowPlayingData> {
        val playbackStateFlow = observePlaybackState()
        val songsFlow = observeQueueUseCase()
        val prefsFlow = getPrefs()
        val paletteFlow = observeCurrentPalette()

        return combine(
            playbackStateFlow,
            prefsFlow,
            songsFlow,
            paletteFlow
        ) { state, prefs, songs, colors ->
            val currentId = state.currentSongId
            val repeatMode = prefs.playbackSettings.repeatMode

            val currentSong = songs.find { it.id == currentId }
            Timber.d("Audily Service Kill - ObserveNowPlayingUseCase - currentSong: ${currentSong?.id} - ${currentSong?.basic?.title} - ${currentSong?.basic?.artist}")
            Timber.d("Chạy khi có sự thay đổi state - ObserveNowPlayingUseCase - currentSong: ${currentSong?.id} - ${currentSong?.basic?.title} - ${currentSong?.basic?.artist}")

            val currentIndex =
                if (currentId != null) songs.indexOfFirst { it.id == currentId } else -1

            val hasNext = when (repeatMode) {
                RepeatMode.OFF, RepeatMode.ONE -> currentIndex < songs.size - 1
                RepeatMode.ALL -> songs.isNotEmpty()
            }

            val hasPrevious = when (repeatMode) {
                RepeatMode.OFF, RepeatMode.ONE -> currentIndex > 0
                RepeatMode.ALL -> songs.isNotEmpty()
            }

            NowPlayingData(
                song = currentSong,
                queue = songs,
                currentIndex = currentIndex,
                playbackState = state.copy(
                    isShuffleOn = prefs.playbackSettings.isShuffleEnabled,
                    repeatMode = repeatMode,
                    speed = prefs.playbackSettings.playbackSpeed,
                    pitch = prefs.playbackSettings.playbackPitch
                ),
                colors = colors,
                hasNext = hasNext,
                hasPrevious = hasPrevious,
                skipDuration = prefs.playbackSettings.skipDuration
            )
        }.filter { data ->
            // Nếu có ID bài hát nhưng chưa load xong Metadata (song == null),
            // thì chặn lại không cho UI cập nhật để tránh hiện tượng giật khung hình.
            data.playbackState.currentSongId == null || data.song != null
        }.distinctUntilChanged()
    }
}
