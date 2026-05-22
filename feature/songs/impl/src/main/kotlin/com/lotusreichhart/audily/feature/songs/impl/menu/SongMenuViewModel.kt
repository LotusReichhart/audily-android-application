package com.lotusreichhart.audily.feature.songs.impl.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.lotusreichhart.audily.core.domain.usecase.favorite.CheckSongFavoriteStatusUseCase
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
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
    private val checkSongFavoriteStatusUseCase: CheckSongFavoriteStatusUseCase,
    private val setRingtoneUseCase: SetRingtoneUseCase,
    private val getSongUseCase: GetSongUseCase,
    private val globalUiEventBus: GlobalUiEventBus
) : ViewModel() {

    private val _params = MutableStateFlow<SongMenuParams?>(null)
    private val _isShowingInfoDialog = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SongMenuUiState?> = _params
        .flatMapLatest { params ->
            if (params == null) {
                flowOf(null)
            } else {
                combine(
                    getSongUseCase(params.song.id),
                    observeNowPlayingUseCase(),
                    checkSongFavoriteStatusUseCase(params.song.id),
                    _isShowingInfoDialog
                ) { fullSong, playbackState, isFavorite, isShowingInfo ->
                    val updatedSong = fullSong ?: params.song
                    val isCurrentSong = playbackState.song?.id == updatedSong.id
                    val isPlaying =
                        playbackState.playbackState.nowPlayingState == NowPlayingState.PLAYING

                    val options = buildMenuOptions(
                        isCurrentSong = isCurrentSong,
                        isPlaying = isPlaying,
                        caller = params.caller,
                        isFavorite = isFavorite,
                        songId = updatedSong.id
                    )
                    SongMenuUiState(
                        song = updatedSong,
                        caller = params.caller,
                        options = options,
                        isShowingInfoDialog = isShowingInfo,
                        isFavorite = isFavorite
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private var _playlistId: Long? = null
    private var _queueIds: List<Long> = emptyList()

    private val _uiEffect = MutableSharedFlow<SongMenuUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun init(
        song: Song,
        playlistId: Long? = null,
        caller: String,
        queueIds: List<Long> = emptyList()
    ) {
        if (_params.value?.song?.id == song.id && _params.value?.caller == caller) return

        this._playlistId = playlistId
        this._queueIds = queueIds
        this._isShowingInfoDialog.value = false

        _params.value = SongMenuParams(song, playlistId, caller, queueIds)
    }

    fun onEvent(event: SongMenuUiEvent) {
        viewModelScope.launch {
            when (event) {
                is SongMenuUiEvent.OnActionClick -> handleAction(event.action)
                SongMenuUiEvent.OnDismissInfoDialog -> {
                    _isShowingInfoDialog.value = false
                }
            }
        }
    }

    private fun buildMenuOptions(
        isCurrentSong: Boolean,
        isPlaying: Boolean,
        caller: String,
        isFavorite: Boolean,
        songId: Long
    ): List<SongMenuAction> {
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

            add(SongMenuAction.ToggleFavorite(isFavorite))
            add(SongMenuAction.ShowInfo)
            add(SongMenuAction.EditTags)
            add(SongMenuAction.Share)
            add(SongMenuAction.Delete)
        }
    }

    private suspend fun handleAction(action: SongMenuAction) {
        val song = uiState.value?.song ?: return
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
                _isShowingInfoDialog.value = true
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

            SongMenuAction.AddToPlaylist -> {
                _uiEffect.emit(SongMenuUiEffect.AddSongToPlaylists(song.id))
            }

            SongMenuAction.Delete -> {

            }

            SongMenuAction.EditTags -> {
                _uiEffect.emit(SongMenuUiEffect.EditTag(song.id))
            }

            SongMenuAction.Share -> {

            }
        }
    }
}

private data class SongMenuParams(
    val song: Song,
    val playlistId: Long?,
    val caller: String,
    val queueIds: List<Long>
)