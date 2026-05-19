package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import javax.inject.Inject

/**
 * UseCase cập nhật thông tin cơ bản của playlist (Tên và Mô tả).
 */
class UpdatePlaylistMetadataUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, name: String, description: String? = null) =
        playlistRepository.updatePlaylist(playlistId, name, description)
}
