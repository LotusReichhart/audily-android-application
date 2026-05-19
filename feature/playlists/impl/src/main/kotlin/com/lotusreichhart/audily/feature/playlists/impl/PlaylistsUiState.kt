package com.lotusreichhart.audily.feature.playlists.impl

import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder

/**
 * Trạng thái giao diện cho màn hình Playlists.
 * playlists nên là một List thay vì Flow để ViewModel có thể quản lý và cập nhật snapshot của dữ liệu.
 */
data class PlaylistsUiState(
    val playlists: List<Playlist> = emptyList(),
    val favoriteCount: Int = 0,
    val favoriteArtworkUris: List<String?> = emptyList(),
    val sortOrder: PlaylistSortOrder = PlaylistSortOrder.NAME,
    val sortType: SortOrderType = SortOrderType.ASC,
    val isLoading: Boolean = false
)
