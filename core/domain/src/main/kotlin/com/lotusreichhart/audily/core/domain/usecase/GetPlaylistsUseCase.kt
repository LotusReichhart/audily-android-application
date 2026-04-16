package com.lotusreichhart.audily.core.domain.usecase

import com.lotusreichhart.audily.core.domain.repository.PlaylistRepository
import com.lotusreichhart.audily.core.model.playlist.Playlist
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    operator fun invoke(): Flow<List<Playlist>> = playlistRepository.getPlaylists()
}
