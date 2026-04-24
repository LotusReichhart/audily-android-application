package com.lotusreichhart.audily.core.data.mapper.song

import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSongsSummary
import com.lotusreichhart.audily.core.model.song.SongsSummary

internal fun MediaStoreSongsSummary.toSongsSummary(): SongsSummary {
    return SongsSummary(
        totalCount = this.count,
        totalDuration = this.totalDuration
    )
}