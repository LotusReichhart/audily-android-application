package com.lotusreichhart.audily.core.data.mapper.prefs

import com.lotusreichhart.audily.core.datastore.AlbumSortOrderProto
import com.lotusreichhart.audily.core.datastore.LibrarySettingsProto
import com.lotusreichhart.audily.core.datastore.PlaylistSortOrderProto
import com.lotusreichhart.audily.core.datastore.SongSortOrderProto
import com.lotusreichhart.audily.core.datastore.SortOrderTypeProto
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import com.lotusreichhart.audily.core.model.prefs.LibrarySettings
import com.lotusreichhart.audily.core.model.song.SongSortOrder

internal fun LibrarySettingsProto.toDomain(): LibrarySettings {
    return LibrarySettings(
        excludedFolders = excludedFoldersList,
        minAudioDuration = if (minAudioDuration > 0) minAudioDuration else 30_000L,
        filterSmallFiles = filterSmallFiles,
        albumGridSize = if (albumGridSize > 0) albumGridSize else 2,
        
        songSortOrder = songSortOrder.toDomain(),
        songSortType = songSortType.toDomain(),
        
        albumSortOrder = albumSortOrder.toDomain(),
        albumSortType = albumSortType.toDomain(),
        
        playlistSortOrder = playlistSortOrder.toDomain(),
        playlistSortType = playlistSortType.toDomain()
    )
}

// === Song Sort ===

internal fun SongSortOrderProto.toDomain(): SongSortOrder = when (this) {
    SongSortOrderProto.SONG_SORT_ORDER_TITLE -> SongSortOrder.TITLE
    SongSortOrderProto.SONG_SORT_ORDER_ARTIST -> SongSortOrder.ARTIST
    SongSortOrderProto.SONG_SORT_ORDER_ALBUM -> SongSortOrder.ALBUM
    SongSortOrderProto.SONG_SORT_ORDER_DURATION -> SongSortOrder.DURATION
    else -> SongSortOrder.DATE_ADDED
}

internal fun SongSortOrder.toProto(): SongSortOrderProto = when (this) {
    SongSortOrder.TITLE -> SongSortOrderProto.SONG_SORT_ORDER_TITLE
    SongSortOrder.ARTIST -> SongSortOrderProto.SONG_SORT_ORDER_ARTIST
    SongSortOrder.ALBUM -> SongSortOrderProto.SONG_SORT_ORDER_ALBUM
    SongSortOrder.DATE_ADDED -> SongSortOrderProto.SONG_SORT_ORDER_DATE_ADDED
    SongSortOrder.DURATION -> SongSortOrderProto.SONG_SORT_ORDER_DURATION
}

// === Album Sort ===

internal fun AlbumSortOrderProto.toDomain(): AlbumSortOrder = when (this) {
    AlbumSortOrderProto.ALBUM_SORT_ORDER_ARTIST -> AlbumSortOrder.ARTIST
    AlbumSortOrderProto.ALBUM_SORT_ORDER_NUMBER_OF_SONGS -> AlbumSortOrder.NUMBER_OF_SONGS
    else -> AlbumSortOrder.NAME
}

internal fun AlbumSortOrder.toProto(): AlbumSortOrderProto = when (this) {
    AlbumSortOrder.NAME -> AlbumSortOrderProto.ALBUM_SORT_ORDER_NAME
    AlbumSortOrder.ARTIST -> AlbumSortOrderProto.ALBUM_SORT_ORDER_ARTIST
    AlbumSortOrder.NUMBER_OF_SONGS -> AlbumSortOrderProto.ALBUM_SORT_ORDER_NUMBER_OF_SONGS
}

// === Playlist Sort ===

internal fun PlaylistSortOrderProto.toDomain(): PlaylistSortOrder = when (this) {
    PlaylistSortOrderProto.PLAYLIST_SORT_ORDER_NUMBER_OF_SONGS -> PlaylistSortOrder.NUMBER_OF_SONGS
    PlaylistSortOrderProto.PLAYLIST_SORT_ORDER_NAME -> PlaylistSortOrder.NAME
    else -> PlaylistSortOrder.CREATED_DATE
}

internal fun PlaylistSortOrder.toProto(): PlaylistSortOrderProto = when (this) {
    PlaylistSortOrder.NAME -> PlaylistSortOrderProto.PLAYLIST_SORT_ORDER_NAME
    PlaylistSortOrder.CREATED_DATE -> PlaylistSortOrderProto.PLAYLIST_SORT_ORDER_CREATED_DATE
    PlaylistSortOrder.NUMBER_OF_SONGS -> PlaylistSortOrderProto.PLAYLIST_SORT_ORDER_NUMBER_OF_SONGS
}

// === Sort Type ===

internal fun SortOrderTypeProto.toDomain(): SortOrderType = when (this) {
    SortOrderTypeProto.SORT_ORDER_TYPE_ASC -> SortOrderType.ASC
    else -> SortOrderType.DESC
}

internal fun SortOrderType.toProto(): SortOrderTypeProto = when (this) {
    SortOrderType.ASC -> SortOrderTypeProto.SORT_ORDER_TYPE_ASC
    SortOrderType.DESC -> SortOrderTypeProto.SORT_ORDER_TYPE_DESC
}
