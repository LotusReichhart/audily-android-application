package com.lotusreichhart.audily.core.network.service

import com.lotusreichhart.audily.core.network.response.LrcLibResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LrcLibService {
    @GET("api/get")
    suspend fun getLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String,
        @Query("album_name") albumName: String?,
        @Query("duration") durationSecs: Int?
    ): LrcLibResponse

    @GET("api/search")
    suspend fun searchLyrics(
        @Query("track_name") trackName: String?,
        @Query("artist_name") artistName: String?,
        @Query("q") query: String? = null
    ): List<LrcLibResponse>
}