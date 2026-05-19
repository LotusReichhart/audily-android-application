package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase lấy toàn bộ danh sách IDs bài hát trong một Playlist.
 * Thường dùng để khởi tạo hàng đợi (Queue) khi bắt đầu phát nhạc từ Playlist.
 */
class GetPlaylistSongIdsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    operator fun invoke(playlistId: Long): Flow<List<Long>> {
        return playlistRepository.getSongIdsInPlaylist(playlistId)
    }
}
