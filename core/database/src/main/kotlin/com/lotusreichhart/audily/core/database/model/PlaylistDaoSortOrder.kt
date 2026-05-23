package com.lotusreichhart.audily.core.database.model

enum class PlaylistDaoSortOrder(val column: String) {
    NAME("name COLLATE LOCALIZED"),
    CREATED_DATE("created_at"),
    NUMBER_OF_SONGS("song_count")
}
