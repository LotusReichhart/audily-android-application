package com.lotusreichhart.audily.core.domain.usecase.playlist

import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * UseCase lấy thông tin Playlist cùng danh sách toàn bộ bài hát bên trong.
 * Trả về List thay vì PagingData để hỗ trợ tính năng sắp xếp lại (Drag & Drop).
 */
class GetPlaylistWithSongsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Long): Flow<Pair<Playlist, List<Song>>?> {
        val playlistFlow = playlistRepository.getPlaylistById(id)
        val songsFlow = playlistRepository.getSongIdsInPlaylist(id)
            .flatMapLatest { ids ->
                songRepository.getSongsByIds(ids)
            }

        return combine(playlistFlow, songsFlow) { playlist, songs ->
            playlist?.let {
                it to songs
            }
        }
    }
}