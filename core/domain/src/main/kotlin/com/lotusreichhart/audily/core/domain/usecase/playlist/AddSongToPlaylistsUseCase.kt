package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import javax.inject.Inject

/**
 * UseCase thêm một bài hát vào danh sách nhiều playlist.
 */
class AddSongToPlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistIds: List<Long>, songId: Long) =
        playlistRepository.addSongToPlaylists(playlistIds, songId)
}
