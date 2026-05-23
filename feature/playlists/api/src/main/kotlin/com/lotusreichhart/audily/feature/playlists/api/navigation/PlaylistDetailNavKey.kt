package com.lotusreichhart.audily.feature.playlists.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Key điều hướng tới màn hình chi tiết Playlist.
 * @param id ID của playlist cần hiển thị.
 */
@Serializable
data class PlaylistDetailNavKey(val id: Long) : NavKey
