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
import com.lotusreichhart.audily.core.model.playback.PlaybackEvent
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.playback.mapper.MediaItemMapper
import com.lotusreichhart.audily.core.playback.mapper.PlaybackStateMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(Main) private val mainDispatcher: CoroutineDispatcher,
    private val listeners: Set<@JvmSuppressWildcards PlaybackStateListener>
) {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private var exoPlayer: ExoPlayer? = null

    private val _playbackState = MutableStateFlow(PlaybackState.INITIAL)
    internal val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    // Lưu trữ ngữ cảnh phát nhạc (Playlist/Album)
    internal var currentSourceId: Long? = null
    internal var currentSourceType: String? = null

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
            else -> { /* Handled at Repository level */
            }
        }
    }

    internal fun release() {
        exoPlayer?.let {
            it.removeListener(playerListener)
            it.release()
        }
        exoPlayer = null
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
    }

    internal fun stop() {
        exoPlayer?.let {
            it.stop()
            it.clearMediaItems()
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
            player.play()
            return
        }

        val currentIndex = player.currentMediaItemIndex

        if (existingIndex != -1) {
            // Trường hợp đã tồn tại trong hàng đợi
            if (existingIndex == currentIndex) return // Đang phát bài này rồi thì thôi

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

    internal fun onSessionEnded() {
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
        return exoPlayer ?: ExoPlayer.Builder(context)
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
            }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) = notifyListeners()
        override fun onIsPlayingChanged(isPlaying: Boolean) = notifyListeners()
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = notifyListeners()
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) = notifyListeners(isDiscontinuity = true)

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            notifyListeners()
        }

        override fun onPlayerError(error: PlaybackException) {
            Timber.e(error, "Player Error: ${error.errorCodeName}")
            exoPlayer?.prepare()
            notifyListeners()
        }
    }

    private fun notifyListeners(isDiscontinuity: Boolean = false) {
        val player = exoPlayer ?: return
        val newState = PlaybackStateMapper.map(player)
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
}
