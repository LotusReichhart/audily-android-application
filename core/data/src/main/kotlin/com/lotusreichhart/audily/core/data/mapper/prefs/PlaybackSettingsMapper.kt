package com.lotusreichhart.audily.core.data.mapper.prefs

import com.lotusreichhart.audily.core.datastore.PlaybackSettingsProto
import com.lotusreichhart.audily.core.datastore.RepeatModeProto
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.model.prefs.PlaybackSettings

internal fun PlaybackSettingsProto.toDomain(): PlaybackSettings {
    return PlaybackSettings(
        skipDuration = if (skipDuration > 0) skipDuration else 10,
        pauseOnUnplug = pauseOnUnplug,
        playbackSpeed = if (playbackSpeed > 0f) playbackSpeed else 1.0f,
        playbackPitch = if (playbackPitch > 0f) playbackPitch else 1.0f,
        volumeNormalization = volumeNormalization,
        isShuffleEnabled = isShuffleEnabled,
        repeatMode = repeatMode.toDomain()
    )
}

internal fun RepeatModeProto.toDomain(): RepeatMode = when (this) {
    RepeatModeProto.REPEAT_MODE_ONE -> RepeatMode.ONE
    RepeatModeProto.REPEAT_MODE_ALL -> RepeatMode.ALL
    else -> RepeatMode.OFF
}

internal fun RepeatMode.toProto(): RepeatModeProto = when (this) {
    RepeatMode.OFF -> RepeatModeProto.REPEAT_MODE_OFF
    RepeatMode.ONE -> RepeatModeProto.REPEAT_MODE_ONE
    RepeatMode.ALL -> RepeatModeProto.REPEAT_MODE_ALL
}
