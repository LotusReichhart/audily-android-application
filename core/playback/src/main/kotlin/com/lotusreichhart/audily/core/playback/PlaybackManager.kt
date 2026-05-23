package com.lotusreichhart.audily.core.playback

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers.Main
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackStateListener
import com.lotusreichhart.audily.core.domain.usecase.history.UpdateHistoryUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.ClearPlaybackSessionUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.model.playback.SleepTimerStatus
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.playback.mapper.MediaItemMapper
import com.lotusreichhart.audily.core.playback.mapper.PlaybackStateMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val HISTORY_THRESHOLD_MS = 60_000L // 1 phút

@Singleton
class PlaybackManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(Main) private val mainDispatcher: CoroutineDispatcher,
    private val listeners: Set<@JvmSuppressWildcards PlaybackStateListener>,
    private val updateHistoryUseCase: UpdateHistoryUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val clearPlaybackSessionUseCase: ClearPlaybackSessionUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private var exoPlayer: ExoPlayer? = null

    private var preferencesJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState.INITIAL)
    internal val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _sleepTimerStatus = MutableStateFlow(SleepTimerStatus.INITIAL)
    internal val sleepTimerStatus: StateFlow<SleepTimerStatus> = _sleepTimerStatus.asStateFlow()

    private var isInitialized = false
    private var isRestoring = false
    private var lastFailedIndex: Int = -1

    // region History Tracking
    private var historyTrackingJob: Job? = null
    private var lastHistorySongId: Long? = null
    // endregion

    // region Sleep Timer
    private var sleepTimerJob: Job? = null
    // endregion

    // Lưu trữ ngữ cảnh phát nhạc (Playlist/Album)
    internal var currentSourceId: Long? = null
    internal var currentSourceType: String? = null

    // Cờ báo hiệu cần khôi phục dữ liệu (dành cho trường hợp Player bị kill nhưng Manager vẫn sống)
    private var needsRestoration = false

    internal val player: Player
        get() = getOrCreatePlayer()

    // region Event Handling

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun handleEvent(event: PlaybackEvent) {
        when (event) {
            is PlaybackEvent.Resume -> play()
            is PlaybackEvent.Pause -> {
                pause()
                notifyListeners() // Kích hoạt lưu session chủ động
            }

            is PlaybackEvent.Stop -> stop()
            is PlaybackEvent.Next -> next()
            is PlaybackEvent.Previous -> previous()
            is PlaybackEvent.SeekTo -> seekTo(event.position)
            is PlaybackEvent.SeekBy -> seekBy(event.offsetMs)
            is PlaybackEvent.SetShuffle -> setShuffle(event.on)
            is PlaybackEvent.SetRepeatMode -> setRepeatMode(event.mode)
            is PlaybackEvent.SetSpeedAndPitch -> setSpeedAndPitch(event.speed, event.pitch)
            is PlaybackEvent.SetQueue -> {
                this.currentSourceId = event.songs.firstOrNull()?.basic?.albumId
                setQueue(event.songs, event.startIndex, event.startPosition)
                play()
            }

            is PlaybackEvent.SeekToIndex -> seekToIndex(event.index)
            is PlaybackEvent.RemoveFromQueue -> removeFromQueue(event.songId)
            is PlaybackEvent.MoveQueueItem -> moveQueueItem(event.from, event.to)
            is PlaybackEvent.AddSongsToQueue -> addSongsToQueue(event.songs, event.index)
            is PlaybackEvent.PlayNext -> addNext(event.song)
            is PlaybackEvent.AddSongToLast -> addLast(event.song)
            is PlaybackEvent.UpdateSongMetadata -> updateSongMetadata(event.song)
        }
    }

    internal fun release() {
        stopHistoryTracking()
        preferencesJob?.cancel()
        preferencesJob = null
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        exoPlayer?.let {
            it.removeListener(playerListener)
            it.release()
        }
        exoPlayer = null
        isInitialized = false
        _playbackState.value = PlaybackState.INITIAL
    }

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun play() {
        val player = getOrCreatePlayer()
        if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
            player.prepare()
        }
        player.play()
        startService()
    }

    internal fun pause() {
        exoPlayer?.pause()
        stopHistoryTracking()
    }

    internal fun stop() {
        stopHistoryTracking()
        exoPlayer?.let {
            it.stop()
            it.clearMediaItems()
        }
        scope.launch {
            clearPlaybackSessionUseCase()
        }
    }

    internal fun next() {
        exoPlayer?.seekToNext()
    }

    internal fun previous() {
        exoPlayer?.seekToPrevious()
    }

    internal fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    internal fun seekToIndex(index: Int) {
        exoPlayer?.seekToDefaultPosition(index)
        exoPlayer?.play()
    }

    internal fun seekBy(offsetMs: Long) {
        val player = exoPlayer ?: return
        val duration =
            if (player.duration == C.TIME_UNSET) Long.MAX_VALUE else player.duration
        val newPosition = (player.currentPosition + offsetMs).coerceIn(0, duration)
        player.seekTo(newPosition)
    }

    // endregion

    // region Queue Management

    internal fun setQueue(songs: List<Song>, startIndex: Int = 0, startPosition: Long = 0) {
        Timber.d("SetQueue Params: $songs - $startIndex - $startPosition")
        val player = getOrCreatePlayer()
        player.setMediaItems(
            MediaItemMapper.toMediaItems(songs),
            startIndex,
            startPosition
        )
        player.prepare()
        markAsInitialized()
    }

    internal fun addNext(song: Song) {
        val player = getOrCreatePlayer()
        val songIdStr = song.id.toString()

        // 1. Kiểm tra xem bài hát đã có trong hàng đợi chưa
        var existingIndex = -1
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == songIdStr) {
                existingIndex = i
                break
            }
        }

        // Nếu player đang trống, thêm vào và phát luôn
        if (player.mediaItemCount == 0) {
            player.setMediaItem(MediaItemMapper.toMediaItem(song))
            player.prepare()
            markAsInitialized()
            player.play()
            return
        }

        val currentIndex = player.currentMediaItemIndex

        if (existingIndex != -1) {
            // Trường hợp đã tồn tại trong hàng đợi
            if (existingIndex == currentIndex) return // Đ đang phát bài này rồi thì thôi

            val targetIndex = if (existingIndex < currentIndex) currentIndex else currentIndex + 1
            if (existingIndex != targetIndex) {
                player.moveMediaItem(
                    existingIndex,
                    targetIndex
                )
            }
        } else {
            // Trường hợp chưa có, thêm mới vào vị trí currentIndex + 1
            player.addMediaItem(
                currentIndex + 1,
                MediaItemMapper.toMediaItem(song)
            )
        }
    }

    internal fun addSongsToQueue(songs: List<Song>, index: Int = -1) {
        val player = getOrCreatePlayer()
        val mediaItems = MediaItemMapper.toMediaItems(songs)

        val targetIndex = if (index >= 0) index else player.mediaItemCount
        player.addMediaItems(targetIndex, mediaItems)
        markAsInitialized()
    }

    internal fun addLast(song: Song) {
        val player = getOrCreatePlayer()
        val songIdStr = song.id.toString()

        // 1. Kiểm tra xem bài hát đã có trong hàng đợi chưa
        var existingIndex = -1
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == songIdStr) {
                existingIndex = i
                break
            }
        }

        if (existingIndex != -1) {
            // Nếu đã tồn tại, di chuyển xuống cuối
            val lastIndex = player.mediaItemCount - 1
            if (existingIndex != lastIndex) {
                player.moveMediaItem(existingIndex, lastIndex)
            }
        } else {
            // Nếu chưa có, thêm mới vào cuối
            player.addMediaItem(MediaItemMapper.toMediaItem(song))
        }
        markAsInitialized()
    }

    internal fun removeFromQueue(songId: Long) {
        val player = exoPlayer ?: return
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == songId.toString()) {
                player.removeMediaItem(i)
                break
            }
        }
    }

    internal fun updateSongMetadata(song: Song) {
        val player = exoPlayer ?: return
        val songIdStr = song.id.toString()
        for (i in 0 until player.mediaItemCount) {
            val mediaItem = player.getMediaItemAt(i)
            if (mediaItem.mediaId == songIdStr) {
                val updatedMediaItem = MediaItemMapper.toMediaItem(song)
                player.replaceMediaItem(i, updatedMediaItem)
                if (i == player.currentMediaItemIndex) {
                    notifyListeners()
                }
            }
        }
    }

    internal fun moveQueueItem(from: Int, to: Int) {
        exoPlayer?.moveMediaItem(
            from,
            to
        )
    }

    internal fun setShuffle(enabled: Boolean) {
        exoPlayer?.shuffleModeEnabled = enabled
    }

    internal fun setRepeatMode(mode: RepeatMode) {
        exoPlayer?.repeatMode = when (mode) {
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
        }
    }

    @OptIn(UnstableApi::class)
    internal fun setSpeedAndPitch(speed: Float, pitch: Float) {
        exoPlayer?.playbackParameters = PlaybackParameters(speed, pitch)
    }

    internal fun needsRestoration(): Boolean = needsRestoration

    internal fun markAsInitialized() {
        if (!isInitialized || needsRestoration) {
            isInitialized = true
            needsRestoration = false
            Timber.d("Audily Service Kill - PlaybackManager marked as initialized")
            if (!isRestoring) {
                notifyListeners()
            }
        }
    }

    internal fun setRestoring(restoring: Boolean) {
        this.isRestoring = restoring
        Timber.d("Audily Service Kill - setRestoring: $restoring")
        if (!restoring) {
            markAsInitialized() // Đảm bảo trạng thái initialized khi kết thúc restore
            notifyListeners()
        }
    }

    internal fun setSleepTimer(durationMs: Long) {
        sleepTimerJob?.cancel()
        if (durationMs <= 0) {
            _sleepTimerStatus.value = SleepTimerStatus.INITIAL
            return
        }

        sleepTimerJob = scope.launch {
            var remaining = durationMs
            _sleepTimerStatus.value = SleepTimerStatus(remaining, true)

            while (remaining > 0) {
                delay(1000)
                remaining -= 1000
                _sleepTimerStatus.value = SleepTimerStatus(remaining.coerceAtLeast(0), true)
            }

            Timber.d("Sleep Timer - Time is up! Pausing playback.")
            pause()
            _sleepTimerStatus.value = SleepTimerStatus.INITIAL
        }
    }

    internal fun onSessionEnded() {
        stopHistoryTracking()
        val player = exoPlayer ?: return
        val queueIds = PlaybackStateMapper.getQueueIds(player)
        val currentMediaItem = player.currentMediaItem
        val songId = currentMediaItem?.mediaId?.toLongOrNull()
        val position = player.currentPosition
        val duration =
            if (player.duration == C.TIME_UNSET) 0L else player.duration

        scope.launch {
            listeners.forEach {
                it.onSessionEnded(
                    songId,
                    position,
                    duration,
                    queueIds,
                    currentSourceId,
                    currentSourceType
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startService() {
        val intent = Intent(context, AudilyAudioService::class.java)
        try {
            context.startService(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start service")
        }
    }

    private fun getOrCreatePlayer(): ExoPlayer {
        val player = exoPlayer
        if (player != null) return player

        return ExoPlayer.Builder(context)
            .setAudioAttributes(
                androidx.media3.common.AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()
            .also {
                it.addListener(playerListener)
                exoPlayer = it
                // Đánh dấu là Player mới toanh, cần nạp lại Session
                needsRestoration = true
                isInitialized = false
                Timber.d("Audily Service Kill - New Player created, needs restoration")

                // Khởi chạy lắng nghe Preferences
                observePreferences()
            }
    }

    @OptIn(UnstableApi::class)
    private fun observePreferences() {
        preferencesJob?.cancel()
        preferencesJob = scope.launch {
            getUserPreferencesUseCase().collect { prefs ->
                val settings = prefs.playbackSettings
                exoPlayer?.let { player ->
                    // 1. Cập nhật Skip Duration
                    player.setSeekBackIncrementMs(settings.skipDuration.toLong() * 1000)
                    player.setSeekForwardIncrementMs(settings.skipDuration.toLong() * 1000)

                    // 2. Cập nhật Playback Parameters (Speed & Pitch)
                    player.playbackParameters = PlaybackParameters(
                        settings.playbackSpeed,
                        settings.playbackPitch
                    )

                    Timber.d("PlaybackManager - Preferences updated: skip=${settings.skipDuration}, speed=${settings.playbackSpeed}")
                }
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            notifyListeners()
            updateHistoryTracking()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            notifyListeners()
            updateHistoryTracking()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            notifyListeners()
            // Reset tracking cho bài hát mới
            lastHistorySongId = null
            updateHistoryTracking()
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) = notifyListeners(isDiscontinuity = true)

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            notifyListeners()
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            notifyListeners()
        }

        override fun onPlayerError(error: PlaybackException) {
            val player = exoPlayer ?: return
            val currentIndex = player.currentMediaItemIndex
            val itemCount = player.mediaItemCount
            val repeatMode = player.repeatMode
            
            val hasNext = currentIndex < itemCount - 1 || repeatMode != Player.REPEAT_MODE_OFF
            val isDecodingError = error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED

            Timber.e(error, "Audily Playback Error: ${error.errorCodeName}. Item: $currentIndex/$itemCount, hasNext: $hasNext")
            
            scope.launch {
                delay(500)
                
                // Nếu lỗi lặp lại lần 2 trên cùng một bài
                if (currentIndex == lastFailedIndex && isDecodingError) {
                    if (hasNext) {
                        Timber.d("Recovery: Second failure on item $currentIndex, skipping to next song.")
                        player.stop()
                        player.seekToNext()
                        player.prepare()
                        player.play()
                        lastFailedIndex = -1 
                    } else {
                        Timber.d("Recovery: Second failure on last item, stopping playback.")
                        player.stop()
                        lastFailedIndex = -1
                    }
                    notifyListeners()
                    return@launch
                }

                // Lần lỗi đầu tiên: Thử cứu bài hiện tại
                lastFailedIndex = currentIndex
                Timber.d("Recovery: Attempting to recover current song (Index $currentIndex)...")
                player.stop()
                player.prepare()
                player.play()
                
                notifyListeners()
            }
        }
    }

    private fun notifyListeners(isDiscontinuity: Boolean = false) {
        if (!isInitialized || isRestoring) {
            Timber.d("Audily Service Kill - notifyListeners blocked: isInitialized=$isInitialized, isRestoring=$isRestoring")
            return
        }
        val player = exoPlayer ?: return
        val newState = PlaybackStateMapper.map(player, true)
        _playbackState.value = newState

        val duration =
            if (player.duration == C.TIME_UNSET) 0L else player.duration

        scope.launch {
            listeners.forEach { listener ->
                if (isDiscontinuity) {
                    listener.onPositionDiscontinuity(
                        newState.currentSongId,
                        player.currentPosition,
                        duration,
                        newState.queueIds,
                        currentSourceId,
                        currentSourceType
                    )
                } else {
                    listener.onPlaybackStateChanged(
                        player.isPlaying,
                        newState.currentSongId,
                        player.currentPosition,
                        duration,
                        newState.queueIds,
                        currentSourceId,
                        currentSourceType
                    )
                }
            }
        }
    }

    // region History Tracking Implementation
    private fun updateHistoryTracking() {
        val player = exoPlayer ?: return
        val currentSongId = player.currentMediaItem?.mediaId?.toLongOrNull() ?: return

        if (player.isPlaying && player.playbackState == Player.STATE_READY) {
            startHistoryTracking(currentSongId)
        } else {
            stopHistoryTracking()
        }
    }

    private fun startHistoryTracking(songId: Long) {
        // Nếu bài này đã được ghi lịch sử trong phiên này rồi thì không đếm lại
        if (lastHistorySongId == songId) return

        // Hủy job cũ nếu có
        historyTrackingJob?.cancel()

        historyTrackingJob = scope.launch {
            Timber.d("Starting history tracking for song $songId")
            var accumulatedTime = 0L
            val pollInterval = 1000L

            while (accumulatedTime < HISTORY_THRESHOLD_MS) {
                delay(pollInterval)
                if (exoPlayer?.isPlaying == true) {
                    accumulatedTime += pollInterval
                }
            }

            Timber.d("History threshold reached for song $songId. Updating history...")
            updateHistoryUseCase(songId)
            lastHistorySongId = songId
        }
    }

    private fun stopHistoryTracking() {
        historyTrackingJob?.cancel()
        historyTrackingJob = null
    }
    // endregion
}
