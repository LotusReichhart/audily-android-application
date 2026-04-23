package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long) = playlistRepository.deletePlaylist(id)
}