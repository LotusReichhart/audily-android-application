package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import javax.inject.Inject

class UpdateDownloadHighResAlbumArtWifiOnlyUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(wifiOnly: Boolean) = repository.updateDownloadHighResAlbumArtWifiOnly(wifiOnly)
}
