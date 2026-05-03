package com.lotusreichhart.audily.feature.home.impl

import com.lotusreichhart.audily.feature.songs.api.R as songsApiR
import com.lotusreichhart.audily.feature.playlists.api.R as playlistsApiR
import com.lotusreichhart.audily.feature.albums.api.R as albumsApiR

internal sealed interface HomeTab {
    val title: Int

    data object Songs : HomeTab {
        override val title: Int = songsApiR.string.feature_songs_api_title
    }

    data object Playlists : HomeTab {
        override val title: Int = playlistsApiR.string.feature_playlists_api_title
    }

    data object Albums : HomeTab {
        override val title: Int = albumsApiR.string.feature_albums_api_title
    }
}
