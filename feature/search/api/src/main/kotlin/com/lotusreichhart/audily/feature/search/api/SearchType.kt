package com.lotusreichhart.audily.feature.search.api

import kotlinx.serialization.Serializable

/**
 * Các loại tìm kiếm được hỗ trợ trong ứng dụng.
 */
@Serializable
enum class SearchType {
    /** Tìm kiếm tất cả: Songs, Albums, Playlists */
    ALL,
    /** Chỉ tìm kiếm bài hát */
    SONGS,
    /** Chỉ tìm kiếm album */
    ALBUMS,
    /** Chỉ tìm kiếm playlist */
    PLAYLISTS
}
