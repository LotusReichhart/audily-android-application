package com.lotusreichhart.audily.feature.songs.api.navigation

import com.lotusreichhart.audily.core.navigation.SingleInstanceKey
import kotlinx.serialization.Serializable

@Serializable
data class SongsPickerNavKey(val playlistId: Long) : SingleInstanceKey
