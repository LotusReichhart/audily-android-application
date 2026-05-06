package com.lotusreichhart.audily.core.data.mapper.history

import com.lotusreichhart.audily.core.database.entity.HistoryEntity
import com.lotusreichhart.audily.core.model.history.History

internal fun HistoryEntity.toHistory(): History {
    return History(
        songId = this.songId,
        playCount = this.playCount,
        lastPlayed = this.lastPlayed
    )
}