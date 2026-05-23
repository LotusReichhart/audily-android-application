package com.lotusreichhart.audily.core.model.song

/**
 * Enum đại diện cho tiêu chí sắp xếp bài hát ở tầng Domain/Shared.
 * Tầng UI sẽ sử dụng enum này để yêu cầu sắp xếp.
 * SongSortOrder: Tiêu chí sort + SortOrderType: Loại sort tăng dần hoặc giảm dần
 */
enum class SongSortOrder {
    TITLE,
    ARTIST,
    ALBUM,
    DATE_ADDED,
    DURATION
}
