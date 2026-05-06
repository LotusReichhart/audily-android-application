package com.lotusreichhart.audily.core.model.home

import com.lotusreichhart.audily.core.model.song.Song

/**
 * Model chứa toàn bộ dữ liệu cho màn hình "Home - Your Vibe".
 */
data class HomeVibe(
    val greetingType: GreetingType,
    val sections: List<HomeSection>,
    val lastPlayedSong: Song? = null
) {
    // Helper để lấy các section theo type dễ dàng hơn ở UI
    val recentHistory: List<Song>
        get() = sections.find { it.type == HomeSectionType.RECENTLY_PLAYED }?.songs ?: emptyList()
    val topPlayed: List<Song>
        get() = sections.find { it.type == HomeSectionType.HEAVY_ROTATION }?.songs ?: emptyList()
    val recentlyAdded: List<Song>
        get() = sections.find { it.type == HomeSectionType.RECENTLY_ADDED }?.songs ?: emptyList()
    val discovery: List<Song> get() = sections.filter { it.isDiscovery }.flatMap { it.songs }
}
