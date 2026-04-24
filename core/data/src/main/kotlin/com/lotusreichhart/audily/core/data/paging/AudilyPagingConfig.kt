package com.lotusreichhart.audily.core.data.paging

import androidx.paging.PagingConfig

/**
 * Cấu hình Paging dùng chung cho toàn bộ ứng dụng.
 */
object AudilyPagingConfig {
    const val PAGE_SIZE = 30
    
    fun defaultConfig(
        pageSize: Int = PAGE_SIZE,
        enablePlaceholders: Boolean = false
    ) = PagingConfig(
        pageSize = pageSize,
        prefetchDistance = pageSize / 2,
        enablePlaceholders = enablePlaceholders,
        initialLoadSize = pageSize * 2
    )
}
