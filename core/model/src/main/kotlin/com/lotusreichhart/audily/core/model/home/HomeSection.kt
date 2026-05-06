package com.lotusreichhart.audily.core.model.home

import com.lotusreichhart.audily.core.model.song.Song

/**
 * Model đại diện cho một phần (section) trên màn hình Home.
 */
data class HomeSection(
    val titleRes: Int = 0,
    val songs: List<Song>,
    val type: HomeSectionType,
    val isDiscovery: Boolean = false
)
