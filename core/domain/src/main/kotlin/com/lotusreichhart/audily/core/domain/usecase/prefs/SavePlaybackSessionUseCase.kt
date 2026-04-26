package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import javax.inject.Inject

class SavePlaybackSessionUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(songId: Long?, position: Long, queueIds: List<Long>) {
        repository.savePlaybackSession(songId, position, queueIds)
    }
}
