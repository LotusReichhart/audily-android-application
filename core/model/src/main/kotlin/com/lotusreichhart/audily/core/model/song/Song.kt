package com.lotusreichhart.audily.core.model.song

/**
 * Dữ liệu mở rộng [extended] có thể null nếu chưa được tải (Lazy loading).
 */
data class Song(
    val id: Long,
    val basic: BasicSongMetadata,
    val extended: ExtendedSongMetadata? = null,
    val isFavorite: Boolean = false,
    val position: Int? = null
) {
    val isPlaceholder: Boolean get() = id == -1L

    companion object {
        val EMPTY = Song(
            id = -1L,
            basic = BasicSongMetadata.EMPTY,
            position = -1
        )
    }
}
