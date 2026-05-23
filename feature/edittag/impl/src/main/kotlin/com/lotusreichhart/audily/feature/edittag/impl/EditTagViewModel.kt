package com.lotusreichhart.audily.feature.edittag.impl

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.edittag.GetEditableSongTagsUseCase
import com.lotusreichhart.audily.core.domain.usecase.edittag.UpdateSongTagsUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongUseCase
import com.lotusreichhart.audily.core.model.edittag.EditTagStatus
import com.lotusreichhart.audily.core.model.edittag.EditableSongTags
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.UpdateQueueItemMetadataUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EditTagViewModel @Inject constructor(
    private val getSongUseCase: GetSongUseCase,
    private val getEditableSongTagsUseCase: GetEditableSongTagsUseCase,
    private val updateSongTagsUseCase: UpdateSongTagsUseCase,
    private val updateQueueItemMetadataUseCase: UpdateQueueItemMetadataUseCase,
    private val globalUiEventBus: GlobalUiEventBus,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTagUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<EditTagUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(event: EditTagUiEvent) {
        when (event) {
            is EditTagUiEvent.Init -> initData(event.songId)
            is EditTagUiEvent.TitleChanged -> _uiState.update { it.copy(title = event.title) }
            is EditTagUiEvent.ArtistChanged -> _uiState.update { it.copy(artist = event.artist) }
            is EditTagUiEvent.AlbumChanged -> _uiState.update { it.copy(album = event.album) }
            is EditTagUiEvent.YearChanged -> _uiState.update { it.copy(year = event.year) }
            is EditTagUiEvent.TrackNumberChanged -> _uiState.update { it.copy(trackNumber = event.trackNumber) }
            is EditTagUiEvent.ComposerChanged -> _uiState.update { it.copy(composer = event.composer) }
            is EditTagUiEvent.GenreChanged -> _uiState.update { it.copy(genre = event.genre) }
            is EditTagUiEvent.ArtworkChanged -> _uiState.update {
                it.copy(
                    artworkBytes = event.bytes,
                    artworkChanged = true,
                    removeArtwork = false
                )
            }
            is EditTagUiEvent.RemoveArtwork -> _uiState.update {
                it.copy(
                    artworkBytes = null,
                    artworkChanged = true,
                    removeArtwork = true
                )
            }
            EditTagUiEvent.SaveClicked -> saveTags()
            EditTagUiEvent.PermissionGranted -> saveTags()
        }
    }

    private fun initData(songId: Long) {
        if (_uiState.value.songId == songId) return

        _uiState.value = EditTagUiState(songId = songId, isLoading = true)

        // Lấy thông tin bài hát từ MediaStore để lấy thông tin hiển thị phụ trợ (như artworkUri ban đầu)
        viewModelScope.launch {
            getSongUseCase(songId).collectLatest { song ->
                if (song != null) {
                    _uiState.update { it.copy(song = song) }
                }
            }
        }

        // Lấy thông tin thẻ tag có thể sửa đổi hiện tại từ file vật lý
        viewModelScope.launch {
            getEditableSongTagsUseCase(songId).collectLatest { tags ->
                if (tags != null) {
                    _uiState.update {
                        it.copy(
                            initialTags = tags,
                            title = tags.title,
                            artist = tags.artist,
                            album = tags.album,
                            year = tags.year?.toString() ?: "",
                            trackNumber = tags.trackNumber?.toString() ?: "",
                            composer = tags.composer ?: "",
                            genre = tags.genre ?: "",
                            artworkBytes = tags.artworkBytes,
                            removeArtwork = tags.removeArtwork,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun saveTags() {
        val songId = _uiState.value.songId ?: return
        val current = _uiState.value

        val targetTags = EditableSongTags(
            title = current.title,
            artist = current.artist,
            album = current.album,
            year = current.year.toIntOrNull(),
            trackNumber = current.trackNumber.toIntOrNull(),
            composer = current.composer.takeIf { it.isNotBlank() },
            genre = current.genre.takeIf { it.isNotBlank() },
            artworkBytes = current.artworkBytes,
            removeArtwork = current.removeArtwork
        )

        viewModelScope.launch {
            updateSongTagsUseCase(songId, targetTags).collect { status ->
                when (status) {
                    is EditTagStatus.Progress -> {
                        _uiState.update {
                            it.copy(
                                isSaving = true,
                                saveProgress = status.progress,
                                permissionIntentSender = null
                            )
                        }
                    }
                    is EditTagStatus.Success -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                saveProgress = null
                            )
                        }
                        globalUiEventBus.emit(
                            GlobalUiEvent.ShowSnackbar(
                                message = UiText.StringResource(R.string.feature_edittag_impl_save_success)
                            )
                        )
                        viewModelScope.launch {
                            val updatedSong = getSongUseCase(songId).firstOrNull { it != null }
                            if (updatedSong != null) {
                                updateQueueItemMetadataUseCase(updatedSong)
                            }
                            _uiEffect.emit(EditTagUiEffect.EditTagSaved)
                        }
                    }
                    is EditTagStatus.NeedScopedStoragePermission -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                saveProgress = null,
                                permissionIntentSender = status.intentSender as? IntentSender
                            )
                        }
                    }
                    is EditTagStatus.Failed -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                saveProgress = null
                            )
                        }
                        globalUiEventBus.emit(
                            GlobalUiEvent.ShowSnackbar(
                                message = UiText.StringResource(R.string.feature_edittag_impl_save_failed)
                            )
                        )
                    }
                }
            }
        }
    }
}