package com.lotusreichhart.audily.feature.edittag.impl

import android.content.IntentSender
import com.lotusreichhart.audily.core.model.edittag.EditableSongTags
import com.lotusreichhart.audily.core.model.song.Song

internal data class EditTagUiState(
    val songId: Long? = null,
    val song: Song? = null,
    val initialTags: EditableSongTags? = null,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val year: String = "",
    val trackNumber: String = "",
    val composer: String = "",
    val genre: String = "",
    val artworkBytes: ByteArray? = null,
    val removeArtwork: Boolean = false,
    val artworkChanged: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveProgress: Float? = null,
    val permissionIntentSender: IntentSender? = null
) {
    val isSaveEnabled: Boolean
        get() = !isLoading && !isSaving && (
            title.trim() != (initialTags?.title ?: "").trim() ||
            artist.trim() != (initialTags?.artist ?: "").trim() ||
            album.trim() != (initialTags?.album ?: "").trim() ||
            year.trim() != (initialTags?.year?.toString() ?: "").trim() ||
            trackNumber.trim() != (initialTags?.trackNumber?.toString() ?: "").trim() ||
            composer.trim() != (initialTags?.composer ?: "").trim() ||
            genre.trim() != (initialTags?.genre ?: "").trim() ||
            artworkChanged ||
            removeArtwork != (initialTags?.removeArtwork ?: false)
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EditTagUiState

        if (songId != other.songId) return false
        if (song != other.song) return false
        if (initialTags != other.initialTags) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (year != other.year) return false
        if (trackNumber != other.trackNumber) return false
        if (composer != other.composer) return false
        if (genre != other.genre) return false
        if (artworkBytes != null) {
            if (other.artworkBytes == null) return false
            if (!artworkBytes.contentEquals(other.artworkBytes)) return false
        } else if (other.artworkBytes != null) return false
        if (removeArtwork != other.removeArtwork) return false
        if (artworkChanged != other.artworkChanged) return false
        if (isLoading != other.isLoading) return false
        if (isSaving != other.isSaving) return false
        if (saveProgress != other.saveProgress) return false
        if (permissionIntentSender != other.permissionIntentSender) return false

        return true
    }

    override fun hashCode(): Int {
        var result = songId?.hashCode() ?: 0
        result = 31 * result + (song?.hashCode() ?: 0)
        result = 31 * result + (initialTags?.hashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + trackNumber.hashCode()
        result = 31 * result + composer.hashCode()
        result = 31 * result + genre.hashCode()
        result = 31 * result + (artworkBytes?.contentHashCode() ?: 0)
        result = 31 * result + removeArtwork.hashCode()
        result = 31 * result + artworkChanged.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + isSaving.hashCode()
        result = 31 * result + (saveProgress?.hashCode() ?: 0)
        result = 31 * result + (permissionIntentSender?.hashCode() ?: 0)
        return result
    }
}
