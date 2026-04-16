package com.lotusreichhart.audily.core.model.song

/**
 * Enum đại diện cho tiêu chí sắp xếp bài hát ở tầng Domain/Shared.
 * Tầng UI sẽ sử dụng enum này để yêu cầu sắp xếp.
 */
enum class SongSortOrder {
    TITLE_ASC,
    TITLE_DESC,
    ARTIST_ASC,
    ARTIST_DESC,
    ALBUM_ASC,
    ALBUM_DESC,
    DATE_ADDED_ASC,
    DATE_ADDED_DESC,
    DURATION_ASC,
    DURATION_DESC
}
