package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.common.SortOrderType
import javax.inject.Inject

class UpdateSongSortTypeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(type: SortOrderType) = repository.updateSongSortType(type)
}
