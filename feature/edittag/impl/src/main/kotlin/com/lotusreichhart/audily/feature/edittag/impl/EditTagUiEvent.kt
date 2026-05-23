package com.lotusreichhart.audily.feature.edittag.impl

internal sealed interface EditTagUiEvent {
    data class Init(val songId: Long) : EditTagUiEvent
    data class TitleChanged(val title: String) : EditTagUiEvent
    data class ArtistChanged(val artist: String) : EditTagUiEvent
    data class AlbumChanged(val album: String) : EditTagUiEvent
    data class YearChanged(val year: String) : EditTagUiEvent
    data class TrackNumberChanged(val trackNumber: String) : EditTagUiEvent
    data class ComposerChanged(val composer: String) : EditTagUiEvent
    data class GenreChanged(val genre: String) : EditTagUiEvent
    data class ArtworkChanged(val bytes: ByteArray?) : EditTagUiEvent
    data object RemoveArtwork : EditTagUiEvent
    data object SaveClicked : EditTagUiEvent
    data object PermissionGranted : EditTagUiEvent
}