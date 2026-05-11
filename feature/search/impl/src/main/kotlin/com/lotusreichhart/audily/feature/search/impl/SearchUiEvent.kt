package com.lotusreichhart.audily.feature.search.impl

import com.lotusreichhart.audily.feature.search.api.SearchType

/**
 * Các sự kiện người dùng từ màn hình Tìm kiếm.
 */
sealed interface SearchUiEvent {
    /** Thay đổi từ khóa tìm kiếm */
    data class OnQueryChange(val query: String) : SearchUiEvent
    /** Thay đổi loại tìm kiếm (All, Songs, Albums, Playlists) */
    data class OnSearchTypeChange(val type: SearchType) : SearchUiEvent
    /** Nhấn vào một bài hát trong kết quả */
    data class OnSongClick(val songId: Long) : SearchUiEvent
    /** Nhấn vào một album trong kết quả */
    data class OnAlbumClick(val albumId: Long) : SearchUiEvent
    /** Nhấn vào một playlist trong kết quả */
    data class OnPlaylistClick(val playlistId: Long) : SearchUiEvent
}
