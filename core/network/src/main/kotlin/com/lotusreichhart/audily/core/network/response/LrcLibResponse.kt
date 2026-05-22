package com.lotusreichhart.audily.core.network.response

import com.google.gson.annotations.SerializedName

data class LrcLibResponse(
    val id: Long,
    val name: String?,
    val trackName: String?,
    val artistName: String?,
    val albumName: String?,
    val duration: Double?,
    val instrumental: Boolean?,
    @SerializedName("plainLyrics") val plainLyrics: String?,
    @SerializedName("syncedLyrics") val syncedLyrics: String?
)