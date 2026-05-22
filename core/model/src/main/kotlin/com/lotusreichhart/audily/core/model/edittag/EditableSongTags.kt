package com.lotusreichhart.audily.core.model.edittag

/**
 * Dữ liệu thẻ tag bài hát có thể chỉnh sửa ở tầng Domain/UI.
 *
 * @param artworkBytes Nội dung byte ảnh bìa mới (null nếu không thay đổi).
 * @param removeArtwork Nếu true, xóa ảnh bìa hiện tại khỏi file.
 */
data class EditableSongTags(
    val title: String,
    val artist: String,
    val album: String,
    val year: Int?,
    val trackNumber: Int?,
    val composer: String?,
    val genre: String?,
    val artworkBytes: ByteArray? = null,
    val removeArtwork: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EditableSongTags

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

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + (year ?: 0)
        result = 31 * result + (trackNumber ?: 0)
        result = 31 * result + (composer?.hashCode() ?: 0)
        result = 31 * result + (genre?.hashCode() ?: 0)
        result = 31 * result + (artworkBytes?.contentHashCode() ?: 0)
        result = 31 * result + removeArtwork.hashCode()
        return result
    }
}
