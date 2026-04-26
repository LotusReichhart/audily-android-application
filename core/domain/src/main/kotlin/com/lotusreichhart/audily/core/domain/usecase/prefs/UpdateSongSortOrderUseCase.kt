package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import javax.inject.Inject

class UpdateSongSortOrderUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(order: SongSortOrder) = repository.updateSongSortOrder(order)
}
