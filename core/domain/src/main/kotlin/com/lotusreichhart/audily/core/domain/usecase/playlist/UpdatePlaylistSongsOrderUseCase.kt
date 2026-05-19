package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import javax.inject.Inject

/**
 * UseCase cập nhật thứ tự các bài hát trong playlist.
 * Được gọi sau khi người dùng thực hiện thao tác kéo thả (Drag & Drop).
 */
class UpdatePlaylistSongsOrderUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, songIds: List<Long>) =
        playlistRepository.updateSongPositionsInPlaylist(playlistId, songIds)
}
