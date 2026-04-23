package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(name: String, description: String? = null) =
        playlistRepository.createPlaylist(name, description)
}