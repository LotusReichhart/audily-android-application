package com.lotusreichhart.audily.core.domain.usecase.playback.palette

import com.lotusreichhart.audily.core.domain.repository.playback.PaletteRepository
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObserveCurrentSongIdUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetBasicSongUseCase
import com.lotusreichhart.audily.core.model.playback.PaletteColors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * UseCase theo dõi và trích xuất màu sắc từ Artwork của bài hát đang phát.
 */
class ObserveCurrentPaletteUseCase @Inject constructor(
    private val observeCurrentSongId: ObserveCurrentSongIdUseCase,
    private val getBasicSong: GetBasicSongUseCase,
    private val paletteRepository: PaletteRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<PaletteColors?> {
        return observeCurrentSongId()
            .flatMapLatest { songId ->
                if (songId == null) return@flatMapLatest flowOf(null)

                getBasicSong(songId)
                    .map { it?.basic?.artworkUri }
                    .distinctUntilChanged()
                    .flatMapLatest { artworkUri ->
                        flow {
                            emit(paletteRepository.extractColors(artworkUri))
                        }
                    }
            }
    }
}