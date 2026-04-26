package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import javax.inject.Inject

class UpdateAlbumSortOrderUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(order: AlbumSortOrder) = repository.updateAlbumSortOrder(order)
}
