package com.lotusreichhart.audily.core.model.lyrics

sealed interface Lyrics {
    val source: LyricsSource

    data class Plain(
        val plainLyrics: PlainLyrics
    ) : Lyrics {
        override val source: LyricsSource get() = plainLyrics.source
    }

    data class Synced(
        val syncedLyrics: SyncedLyrics
    ) : Lyrics {
        override val source: LyricsSource get() = syncedLyrics.source
    }
}
