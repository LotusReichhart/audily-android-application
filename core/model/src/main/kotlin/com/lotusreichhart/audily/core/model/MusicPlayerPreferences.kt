package com.lotusreichhart.audily.core.model

enum class RepeatMode {
    Off,
    One,
    All
}

data class MusicPlayerPreferences(
    val repeatMode: RepeatMode,
    val shuffleMode: Boolean
)
