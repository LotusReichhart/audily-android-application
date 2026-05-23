package com.lotusreichhart.audily.feature.albums.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDetailNavKey(val id: Long) : NavKey
