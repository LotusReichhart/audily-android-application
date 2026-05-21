package com.lotusreichhart.audily.feature.playlists.api.navigation

import com.lotusreichhart.audily.core.navigation.SingleInstanceKey
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistsPickerNavKey(val songId: Long) : SingleInstanceKey
