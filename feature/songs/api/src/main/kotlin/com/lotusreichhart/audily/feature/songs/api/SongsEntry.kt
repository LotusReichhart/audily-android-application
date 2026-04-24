package com.lotusreichhart.audily.feature.songs.api

import com.lotusreichhart.audily.core.navigation.FeatureEntry

/**
 * Entry point cho tính năng danh sách Bài hát.
 * Được định nghĩa ở module API để các module khác (như Home) có thể gọi mà không phụ thuộc vào Impl.
 */
interface SongsEntry : FeatureEntry
