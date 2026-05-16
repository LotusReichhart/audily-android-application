package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import javax.inject.Inject

/**
 * UseCase tạo mới một playlist.
 * Trả về [Result] chứa ID của playlist mới nếu thành công, hoặc exception nếu thất bại.
 */
class CreatePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(name: String, description: String? = null): Result<Long> =
        runCatching {
            playlistRepository.createPlaylist(name, description)
        }
}