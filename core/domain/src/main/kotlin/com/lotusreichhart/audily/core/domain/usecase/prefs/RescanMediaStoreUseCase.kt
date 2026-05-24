package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase quét lại MediaStore bằng cách ủy quyền cho repository.
 */
class RescanMediaStoreUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        repository.rescanMediaStore()
    }
}
