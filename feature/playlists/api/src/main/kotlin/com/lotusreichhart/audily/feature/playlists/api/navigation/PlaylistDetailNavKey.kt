package com.lotusreichhart.audily.feature.playlists.api.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Key điều hướng tới màn hình chi tiết Playlist.
 * @param id ID của playlist cần hiển thị.
 */
data class PlaylistDetailNavKey(val id: Long) : NavKey
