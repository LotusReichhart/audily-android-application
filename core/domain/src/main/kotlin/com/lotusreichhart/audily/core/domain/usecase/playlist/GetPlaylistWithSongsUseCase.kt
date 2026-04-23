package com.lotusreichhart.audily.core.domain.usecase.playlist

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.song.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPlaylistWithSongsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(id: Long): Flow<Pair<Playlist, PagingData<Song>>?> {
        return combine(
            playlistRepository.getPlaylistById(id),
            playlistRepository.getPlaylistSongsPaged(id)
        ) { playlist, pagingData ->
            playlist?.let {
                it to pagingData
            }
        }
    }
}