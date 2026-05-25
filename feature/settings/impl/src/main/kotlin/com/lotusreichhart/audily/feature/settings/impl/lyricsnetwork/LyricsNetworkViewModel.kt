package com.lotusreichhart.audily.feature.settings.impl.lyricsnetwork

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdatePreferEmbeddedOfflineLyricsUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateDefaultLyricsSourceUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateDownloadHighResAlbumArtWifiOnlyUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateFetchMissingArtistImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LyricsNetworkViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updatePreferEmbeddedOfflineLyricsUseCase: UpdatePreferEmbeddedOfflineLyricsUseCase,
    private val updateDefaultLyricsSourceUseCase: UpdateDefaultLyricsSourceUseCase,
    private val updateDownloadHighResAlbumArtWifiOnlyUseCase: UpdateDownloadHighResAlbumArtWifiOnlyUseCase,
    private val updateFetchMissingArtistImagesUseCase: UpdateFetchMissingArtistImagesUseCase
) : ViewModel() {

    val uiState: StateFlow<LyricsNetworkUiState> = getUserPreferencesUseCase()
        .map {
            LyricsNetworkUiState(
                isLoading = false,
                preferEmbeddedOfflineLyrics = it.lyricsNetworkSettings.preferEmbeddedOfflineLyrics,
                defaultLyricsSource = it.lyricsNetworkSettings.defaultLyricsSource,
                downloadHighResAlbumArtWifiOnly = it.lyricsNetworkSettings.downloadHighResAlbumArtWifiOnly,
                fetchMissingArtistImages = it.lyricsNetworkSettings.fetchMissingArtistImages
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LyricsNetworkUiState()
        )

    fun onEvent(event: LyricsNetworkUiEvent) {
        viewModelScope.launch {
            when (event) {
                is LyricsNetworkUiEvent.OnPreferEmbeddedOfflineLyricsChanged -> {
                    updatePreferEmbeddedOfflineLyricsUseCase(event.prefer)
                }
                is LyricsNetworkUiEvent.OnDefaultLyricsSourceChanged -> {
                    updateDefaultLyricsSourceUseCase(event.source)
                }
                is LyricsNetworkUiEvent.OnDownloadHighResAlbumArtWifiOnlyChanged -> {
                    updateDownloadHighResAlbumArtWifiOnlyUseCase(event.wifiOnly)
                }
                is LyricsNetworkUiEvent.OnFetchMissingArtistImagesChanged -> {
                    updateFetchMissingArtistImagesUseCase(event.fetch)
                }
            }
        }
    }
}