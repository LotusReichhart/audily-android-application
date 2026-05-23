package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import javax.inject.Inject

/**
 * UseCase thêm danh sách bài hát vào một playlist.
 * Các bài hát mới sẽ được thêm vào cuối danh sách hiện tại.
 */
class AddSongsToPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, songIds: List<Long>) =
        playlistRepository.addSongsToPlaylist(playlistId, songIds)
}
