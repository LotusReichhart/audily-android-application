package com.lotusreichhart.audily.core.model.prefs

import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import com.lotusreichhart.audily.core.model.song.SongSortOrder

/**
 * Các thiết lập liên quan đến thư viện nhạc (Persistent).
 */
data class LibrarySettings(
    val excludedFolders: List<String> = emptyList(),
    val minAudioDuration: Long = 30_000, // Bỏ qua file < 30s
    val filterSmallFiles: Boolean = true,
    val albumGridSize: Int = 2,
    
    // Sort preferences
    val songSortOrder: SongSortOrder = SongSortOrder.DATE_ADDED,
    val songSortType: SortOrderType = SortOrderType.DESC,
    
    val albumSortOrder: AlbumSortOrder = AlbumSortOrder.NAME,
    val albumSortType: SortOrderType = SortOrderType.ASC,
    
    val playlistSortOrder: PlaylistSortOrder = PlaylistSortOrder.CREATED_DATE,
    val playlistSortType: SortOrderType = SortOrderType.DESC
)
