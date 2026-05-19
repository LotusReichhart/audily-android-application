package com.lotusreichhart.audily.feature.songs.impl.menu

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PauseSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.AddSongToQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayNextUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObserveNowPlayingUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.RemoveSongFromPlaylistUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.SetRingtoneUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.song.RingtoneResult
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.ui.GlobalMenuCaller
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongMenuViewModel @Inject constructor(
    private val observeNowPlayingUseCase: ObserveNowPlayingUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val playNextUseCase: PlayNextUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
    private val addSongToQueueUseCase: AddSongToQueueUseCase,
    private val removeSongFromPlaylistUseCase: RemoveSongFromPlaylistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val setRingtoneUseCase: SetRingtoneUseCase,
    private val getSongUseCase: GetSongUseCase,
    private val globalUiEventBus: GlobalUiEventBus
) : ViewModel() {

    private val _uiState = mutableStateOf<SongMenuUiState?>(null)
    val uiState: State<SongMenuUiState?> = _uiState
    private var _playlistId: Long? = null
    private var _queueIds: List<Long> = emptyList()

    fun init(
        song: Song,
        playlistId: Long? = null,
        caller: String,
        queueIds: List<Long> = emptyList()
    ) {
        if (_uiState.value?.song?.id == song.id && _uiState.value?.caller == caller) return

        this._playlistId = playlistId
        this._queueIds = queueIds

        // Khởi tạo trạng thái với các options mặc định để tránh Menu trống trong lần đầu app chạy
        _uiState.value = SongMenuUiState(
            song = song,
            caller = caller,
            options = buildMenuOptions(isCurrentSong = false, isPlaying = false, caller = caller)
        )

        // Tải thông tin đầy đủ của bài hát từ Database
        getSongUseCase(song.id)
            .onEach { fullSong ->
                if (fullSong != null) {
                    val state = _uiState.value ?: return@onEach
                    _uiState.value = state.copy(song = fullSong)
                }
            }
            .launchIn(viewModelScope)

        // Observe playback state to update Play/Pause icon in real-time
        observeNowPlayingUseCase()
            .onEach { playbackState ->
                val state = _uiState.value ?: return@onEach
                val isCurrentSong = playbackState.song?.id == state.song.id
                val isPlaying =
                    playbackState.playbackState.nowPlayingState == NowPlayingState.PLAYING

                _uiState.value = state.copy(
                    options = buildMenuOptions(isCurrentSong, isPlaying, state.caller)
                )
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: SongMenuUiEvent) {
        viewModelScope.launch {
            when (event) {
                is SongMenuUiEvent.OnActionClick -> handleAction(event.action)
                SongMenuUiEvent.OnDismissInfoDialog -> {
                    val state = _uiState.value ?: return@launch
                    _uiState.value = state.copy(isShowingInfoDialog = false)
                }
            }
        }
    }

    private fun buildMenuOptions(
        isCurrentSong: Boolean,
        isPlaying: Boolean,
        caller: String
    ): List<SongMenuAction> {
        val songId = _uiState.value?.song?.id ?: 0L

        return buildList {
            when (caller) {
                GlobalMenuCaller.NOW_PLAYING -> {
                    // Trong NowPlaying: Luôn là bài đang phát
                    add(SongMenuAction.ResumePause(isPlaying))
                    add(SongMenuAction.AddToPlaylist)
                    add(SongMenuAction.SetRingtone)
                }

                GlobalMenuCaller.PLAYLIST -> {
                    if (isCurrentSong) {
                        // Bài đang phát: Chỉ hiện ResumePause
                        add(SongMenuAction.ResumePause(isPlaying))
                        add(SongMenuAction.SetRingtone)
                        add(SongMenuAction.RemoveFromPlaylist(_playlistId, songId))
                    } else {
                        // Bài khác: Hiện Play, PlayNext, AddToQueue
                        add(SongMenuAction.Play(songId, _queueIds))
                        add(SongMenuAction.PlayNext)
                        add(SongMenuAction.AddToQueue)
                        add(SongMenuAction.SetRingtone)
                        add(SongMenuAction.RemoveFromPlaylist(_playlistId, songId))
                    }
                }

                GlobalMenuCaller.LIST_SCREEN -> {
                    if (isCurrentSong) {
                        // Bài đang phát: Chỉ hiện ResumePause
                        add(SongMenuAction.ResumePause(isPlaying))
                        add(SongMenuAction.AddToPlaylist)
                        add(SongMenuAction.SetRingtone)
                    } else {
                        // Bài khác: Hiện Play, PlayNext, AddToQueue
                        add(SongMenuAction.Play(songId, _queueIds))
                        add(SongMenuAction.PlayOnce)
                        add(SongMenuAction.PlayNext)
                        add(SongMenuAction.AddToQueue)
                        add(SongMenuAction.AddToPlaylist)
                        add(SongMenuAction.SetRingtone)
                    }
                }
            }

            add(SongMenuAction.ToggleFavorite(_uiState.value?.song?.isFavorite ?: false))
            add(SongMenuAction.ShowInfo)
            add(SongMenuAction.EditTags)
            add(SongMenuAction.Share)
            add(SongMenuAction.Delete)
        }
    }

    private suspend fun handleAction(action: SongMenuAction) {
        val song = _uiState.value?.song ?: return
        when (action) {
            is SongMenuAction.Play -> {
                playFromQueueUseCase(action.songId, action.queueIds)
            }

            is SongMenuAction.PlayOnce -> {
                playFromQueueUseCase(song.id, emptyList())
            }

            is SongMenuAction.ResumePause -> {
                if (action.isPlaying) pauseSongUseCase() else resumeSongUseCase()
            }

            is SongMenuAction.ToggleFavorite -> toggleFavoriteUseCase(song.id)

            is SongMenuAction.RemoveFromPlaylist -> {
                if (action.playlistId == null) return
                viewModelScope.launch {
                    removeSongFromPlaylistUseCase(action.playlistId, song.id)
                }
            }

            SongMenuAction.PlayNext -> playNextUseCase(song)
            SongMenuAction.AddToQueue -> addSongToQueueUseCase(song)
            SongMenuAction.ShowInfo -> {
                val state = _uiState.value ?: return
                _uiState.value = state.copy(isShowingInfoDialog = true)
            }

            SongMenuAction.SetRingtone -> {
                viewModelScope.launch {
                    when (val result = setRingtoneUseCase(song.id)) {
                        is RingtoneResult.SUCCESS -> {
                            globalUiEventBus.emit(GlobalUiEvent.HideSheet)
                            globalUiEventBus.emit(GlobalUiEvent.ShowSnackbar(UiText.DynamicString("Successfully set as ringtone")))
                        }

                        is RingtoneResult.NO_PERMISSION -> {
                            globalUiEventBus.emit(
                                GlobalUiEvent.ShowSnackbar(
                                    message = UiText.DynamicString("Need Write Settings permission"),
                                    actionLabel = UiText.DynamicString("GRANT"),
                                    onAction = {
                                        globalUiEventBus.emit(GlobalUiEvent.OpenWriteSettingsPermission)
                                    }
                                )
                            )
                        }

                        is RingtoneResult.NEED_SCOPED_STORAGE_PERMISSION -> {
                            globalUiEventBus.emit(GlobalUiEvent.RequestFilePermission(result.intentSender))
                        }

                        is RingtoneResult.FAILED -> {
                            globalUiEventBus.emit(GlobalUiEvent.ShowSnackbar(UiText.DynamicString("Failed to set ringtone")))
                        }
                    }
                }
            }

            else -> {}
        }
    }
}