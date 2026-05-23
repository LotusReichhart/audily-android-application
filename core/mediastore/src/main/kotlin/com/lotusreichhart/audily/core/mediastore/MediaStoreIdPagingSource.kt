package com.lotusreichhart.audily.core.mediastore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreSong
import timber.log.Timber

/**
 * PagingSource dựa trên danh sách ID đã được sắp xếp sẵn.
 * Giúp đảm bảo thứ tự hiển thị chính xác khi thực hiện sorting trong bộ nhớ.
 */
class MediaStoreIdPagingSource(
    private val dataSources: MediaStoreDataSource,
    private val sortedIds: List<Long>
) : PagingSource<Int, MediaStoreSong>() {

    override fun getRefreshKey(state: PagingState<Int, MediaStoreSong>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaStoreSong> {
        return try {
            val position = params.key ?: 0
            val loadSize = params.loadSize
            
            // Tính toán dải ID cần lấy cho trang hiện tại
            val end = (position + loadSize).coerceAtMost(sortedIds.size)
            if (position >= sortedIds.size) {
                return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            }
            
            val idsToLoad = sortedIds.subList(position, end)
            
            // Lấy dữ liệu chi tiết cho từng ID
            val songs = idsToLoad.mapNotNull { id ->
                dataSources.getSong(id)
            }

            Timber.d("Loaded ${songs.size} songs for page at position $position (ID-based)")

            LoadResult.Page(
                data = songs,
                prevKey = if (position == 0) null else position - loadSize,
                nextKey = if (end >= sortedIds.size) null else end
            )
        } catch (e: Exception) {
            Timber.e(e, "Error loading ID-based paging")
            LoadResult.Error(e)
        }
    }
}
