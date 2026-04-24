package com.lotusreichhart.audily.feature.songs.impl

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import kotlinx.coroutines.flow.Flow

/**
 * Các trạng thái UI của màn hình Bài hát.
 */
internal sealed interface SongsUiState {
    /**
     * Trạng thái đang tải dữ liệu ban đầu.
     */
    data object Loading : SongsUiState

    /**
     * Trạng thái tải dữ liệu thành công.
     */
    data class Success(
        val songs: Flow<PagingData<Song>>,
        val summary: SongsSummary = SongsSummary(),
        val sortOrder: SongSortOrder = SongSortOrder.TITLE_ASC
    ) : SongsUiState
}