package com.lotusreichhart.audily.core.domain.usecase.playback.state

import com.lotusreichhart.audily.core.domain.repository.playback.PaletteRepository
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetBasicSongsUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingData
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * UseCase gộp dữ liệu cho màn hình Now Playing.
 * Kết hợp thông tin từ PlaybackState, UserPreferences và trích xuất PaletteColors.
 */
class ObserveNowPlayingUseCase @Inject constructor(
    private val observePlaybackState: ObservePlaybackStateUseCase,
    private val getBasicSongs: GetBasicSongsUseCase,
    private val paletteRepository: PaletteRepository,
    private val getPrefs: GetUserPreferencesUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<NowPlayingData> {
        val playbackStateFlow = observePlaybackState()
        val prefsFlow = getPrefs()

        // Chỉ tải metadata khi danh sách ID trong hàng đợi thay đổi
        val songsFlow = playbackStateFlow
            .map { it.queueIds }
            .distinctUntilChanged()
            .flatMapLatest { ids ->
                if (ids.isNotEmpty()) getBasicSongs(ids) else flowOf(emptyList())
            }

        return combine(
            playbackStateFlow,
            prefsFlow,
            songsFlow
        ) { state, prefs, songs ->
            val currentId = state.currentSongId
            val repeatMode = prefs.playbackSettings.repeatMode

            val currentSong = songs.find { it.id == currentId }
            val currentIndex =
                if (currentId != null) songs.indexOfFirst { it.id == currentId } else -1

            val colors =
                currentSong?.basic?.artworkUri?.let { paletteRepository.extractColors(it) }

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
                    repeatMode = repeatMode
                ),
                colors = colors,
                hasNext = hasNext,
                hasPrevious = hasPrevious,
                skipDuration = prefs.playbackSettings.skipDuration
            )
        }
    }
}