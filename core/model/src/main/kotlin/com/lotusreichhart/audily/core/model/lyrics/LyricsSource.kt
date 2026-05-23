package com.lotusreichhart.audily.core.model.lyrics

/**
 * Các loại nguồn lấy lời bài hát.
 */
enum class LyricsSource {
    EMBEDDED,    // Lấy từ metadata của file nhạc
    REMOTE,      // Lấy từ server hoặc Internet
    LOCAL_FILE   // Lấy từ file .lrc hoặc .txt riêng biệt
}
