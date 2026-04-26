package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.playlist.PlaylistSortOrder
import javax.inject.Inject

class UpdatePlaylistSortOrderUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(order: PlaylistSortOrder) = repository.updatePlaylistSortOrder(order)
}
