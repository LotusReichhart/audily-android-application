package com.lotusreichhart.audily.core.database.model

/**
 * Tiêu chí sắp xếp Playlist ở tầng Database.
 * Tự mang theo chuỗi SQL để tối ưu hóa truy vấn RawQuery.
 */
enum class PlaylistDaoSortOrder(val sqlOrder: String) {
    NAME_ASC("name COLLATE LOCALIZED ASC"),
    NAME_DESC("name COLLATE LOCALIZED DESC"),
    CREATED_DATE_ASC("created_at ASC"),
    CREATED_DATE_DESC("created_at DESC")
}