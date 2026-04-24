package com.lotusreichhart.audily.feature.songs.impl

import com.lotusreichhart.audily.core.model.song.SongSortOrder

/**
 * Các hành động từ UI gửi tới ViewModel của màn hình Bài hát.
 */
internal sealed interface SongsUiEvent {
    /**
     * Thay đổi thứ tự sắp xếp danh sách.
     */
    data class SortOrderChanged(val sortOrder: SongSortOrder) : SongsUiEvent

    /**
     * Khi người dùng nhấn vào một bài hát.
     */
    data class SongClicked(val songId: Long) : SongsUiEvent
    
    // TODO: (Sprint 2.4) Thêm các event khác: Refresh, AddToPlaylist, Delete, vv.
}
