package com.lotusreichhart.audily.feature.favorites.impl

internal sealed interface FavoritesUiEvent {
    data class SongClicked(val songId: Long, val isMissing: Boolean) : FavoritesUiEvent
    data class RemoveSong(val songId: Long) : FavoritesUiEvent
    object PlayAll : FavoritesUiEvent
    object DeleteAll : FavoritesUiEvent
}