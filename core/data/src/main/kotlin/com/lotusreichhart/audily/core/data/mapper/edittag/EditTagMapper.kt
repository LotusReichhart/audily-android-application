package com.lotusreichhart.audily.core.data.mapper.edittag

import com.lotusreichhart.audily.core.mediastore.model.MediaStoreEditTagStatus
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreEditableTags
import com.lotusreichhart.audily.core.model.edittag.EditTagStatus
import com.lotusreichhart.audily.core.model.edittag.EditableSongTags

/**
 * Ánh xạ EditableSongTags (Domain) sang MediaStoreEditableTags (DataSource).
 */
internal fun EditableSongTags.toMediaStoreEditableTags(): MediaStoreEditableTags {
    return MediaStoreEditableTags(
        title = title,
        artist = artist,
        album = album,
        year = year,
        trackNumber = trackNumber,
        composer = composer,
        genre = genre,
        artworkBytes = artworkBytes,
        removeArtwork = removeArtwork
    )
}

/**
 * Ánh xạ MediaStoreEditableTags (DataSource) sang EditableSongTags (Domain).
 */
internal fun MediaStoreEditableTags.toEditableSongTags(): EditableSongTags {
    return EditableSongTags(
        title = title,
        artist = artist,
        album = album,
        year = year,
        trackNumber = trackNumber,
        composer = composer,
        genre = genre,
        artworkBytes = artworkBytes,
        removeArtwork = removeArtwork
    )
}

/**
 * Ánh xạ MediaStoreEditTagStatus (DataSource) sang EditTagStatus (Domain).
 */
internal fun MediaStoreEditTagStatus.toEditTagStatus(): EditTagStatus {
    return when (this) {
        is MediaStoreEditTagStatus.Progress -> EditTagStatus.Progress(progress)
        is MediaStoreEditTagStatus.Success -> EditTagStatus.Success
        is MediaStoreEditTagStatus.NeedScopedStoragePermission ->
            EditTagStatus.NeedScopedStoragePermission(intentSender)
        is MediaStoreEditTagStatus.Failed -> EditTagStatus.Failed
    }
}
