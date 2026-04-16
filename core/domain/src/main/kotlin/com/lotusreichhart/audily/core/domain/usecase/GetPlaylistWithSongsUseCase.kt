package com.lotusreichhart.audily.core.domain.usecase

import com.lotusreichhart.audily.core.domain.repository.PlaylistRepository
import com.lotusreichhart.audily.core.model.playlist.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPlaylistWithSongsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(id: Long): Flow<PlaylistWithSongs?> {
        return combine(
            playlistRepository.getPlaylistById(id),
            playlistRepository.getSongsInPlaylist(id)
        ) { playlist, songs ->
            playlist?.let {
                PlaylistWithSongs(it, songs)
            }
        }
    }
}