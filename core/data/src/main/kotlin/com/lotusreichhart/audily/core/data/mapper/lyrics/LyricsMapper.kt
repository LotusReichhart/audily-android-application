package com.lotusreichhart.audily.core.data.mapper.lyrics

import com.lotusreichhart.audily.core.database.entity.LyricsEntity
import com.lotusreichhart.audily.core.model.lyrics.Lyrics
import com.lotusreichhart.audily.core.model.lyrics.LyricsSource
import com.lotusreichhart.audily.core.model.lyrics.PlainLyrics
import com.lotusreichhart.audily.core.model.lyrics.SyncedLyrics
import com.lotusreichhart.audily.core.model.lyrics.SyncedLyricsSegment

/**
 * Phân tích cú pháp và chuyển đổi chuỗi văn bản lời bài hát thô (có thể chứa timestamp LRC)
 * thành đối tượng domain [Lyrics].
 */
fun String.toLyrics(source: LyricsSource): Lyrics {
    val lines = this.lines()
    val segments = mutableListOf<SyncedLyricsSegment>()
    // Regex nhận diện [mm:ss.xx] hoặc [mm:ss:xx] hoặc [mm:ss]
    val regex = Regex("""\[(\d+):(\d+)(?:[.:](\d+))?\](.*)""")

    for (line in lines) {
        val trimmed = line.trim()
        val match = regex.find(trimmed)
        if (match != null) {
            val min = match.groupValues[1].toLong()
            val sec = match.groupValues[2].toLong()
            val msPart = match.groupValues[3].takeIf { it.isNotEmpty() }?.toLong() ?: 0L
            val text = match.groupValues[4].trim()

            val msMultiplier = if (match.groupValues[3].length == 2) 10 else 1
            val startTimeMs = min * 60 * 1000 + sec * 1000 + msPart * msMultiplier

            segments.add(SyncedLyricsSegment(text, startTimeMs))
        }
    }

    return if (segments.isNotEmpty()) {
        Lyrics.Synced(
            SyncedLyrics(
                segments = segments.sortedBy { it.startTimeMillis },
                source = source
            )
        )
    } else {
        Lyrics.Plain(
            PlainLyrics(
                content = this,
                source = source
            )
        )
    }
}

/**
 * Chuyển đổi [LyricsEntity] từ cơ sở dữ liệu thành đối tượng domain [Lyrics].
 */
fun LyricsEntity.toLyrics(): Lyrics {
    val parsedSource = try {
        LyricsSource.valueOf(source)
    } catch (e: Exception) {
        LyricsSource.REMOTE
    }
    return content.toLyrics(parsedSource)
}
