package com.lotusreichhart.audily.feature.songs.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SongsPickerNavKey(val id: Long) : NavKey
