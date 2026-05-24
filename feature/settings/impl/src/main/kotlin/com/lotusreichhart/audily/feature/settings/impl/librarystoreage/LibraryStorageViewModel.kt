package com.lotusreichhart.audily.feature.settings.impl.librarystoreage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.history.ClearAllHistoryUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.RescanMediaStoreUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateExcludedFoldersUseCase
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import com.lotusreichhart.audily.feature.settings.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class LibraryStorageViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateExcludedFoldersUseCase: UpdateExcludedFoldersUseCase,
    private val rescanMediaStoreUseCase: RescanMediaStoreUseCase,
    private val clearAllHistoryUseCase: ClearAllHistoryUseCase,
    private val globalUiEventBus: GlobalUiEventBus
) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)

    val uiState: StateFlow<LibraryStorageUiState> = combine(
        getUserPreferencesUseCase().map { it.librarySettings.excludedFolders },
        _isScanning
    ) { excludedFolders, isScanning ->
        LibraryStorageUiState(
            excludedFolders = excludedFolders,
            isScanning = isScanning
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryStorageUiState()
    )

    fun onEvent(event: LibraryStorageUiEvent) {
        viewModelScope.launch {
            when (event) {
                is LibraryStorageUiEvent.AddExcludedFolder -> {
                    val current = uiState.value.excludedFolders
                    if (!current.contains(event.path)) {
                        updateExcludedFoldersUseCase(current + event.path)
                    }
                }

                is LibraryStorageUiEvent.RemoveExcludedFolder -> {
                    val current = uiState.value.excludedFolders
                    updateExcludedFoldersUseCase(current - event.path)
                }

                is LibraryStorageUiEvent.RescanMediaStore -> {
                    if (_isScanning.value) return@launch
                    _isScanning.value = true
                    globalUiEventBus.emit(
                        GlobalUiEvent.ShowSnackbar(
                            message = UiText.StringResource(R.string.feature_settings_impl_library_scanning_msg)
                        )
                    )
                    try {
                        rescanMediaStoreUseCase()
                        globalUiEventBus.emit(
                            GlobalUiEvent.ShowSnackbar(
                                message = UiText.StringResource(R.string.feature_settings_impl_library_scan_complete_msg)
                            )
                        )
                    } catch (e: Exception) {
                        Timber.e("RescanMediaStore error: $e")
                    } finally {
                        _isScanning.value = false
                    }
                }

                is LibraryStorageUiEvent.ClearAllHistory -> {
                    clearAllHistoryUseCase()
                }
            }
        }
    }
}