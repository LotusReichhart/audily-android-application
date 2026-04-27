package com.lotusreichhart.audily.core.playback

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val listeners: Set<@JvmSuppressWildcards PlaybackStateListener>,
    @param:Dispatcher(Main) private val mainDispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)

    private var exoPlayer: ExoPlayer? = null

    private val _playbackState = MutableStateFlow(PlaybackState.INITIAL)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    val player: Player
        get() = getOrCreatePlayer()

    private fun getOrCreatePlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context)
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
    }

    private fun notifyListeners(isDiscontinuity: Boolean = false) {
        val player = exoPlayer ?: return
        val newState = PlaybackStateMapper.map(player)
        _playbackState.value = newState

        scope.launch {
            listeners.forEach { listener ->
                if (isDiscontinuity) {
                    listener.onPositionDiscontinuity(newState.currentSongId, newState.playbackPosition, newState.queueIds)
                } else {
                    listener.onPlaybackStateChanged(player.isPlaying, newState.currentSongId, newState.playbackPosition, newState.queueIds)
                }
            }
        }
    }

    fun play(song: Song) {
        val player = getOrCreatePlayer()
        player.setMediaItem(MediaItemMapper.toMediaItem(song))
        player.prepare()
        player.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun resume() {
        exoPlayer?.play()
    }

    fun stop() {
        exoPlayer?.stop()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun seekBy(offsetMs: Long) {
        val player = exoPlayer ?: return
        val duration = if (player.duration == androidx.media3.common.C.TIME_UNSET) Long.MAX_VALUE else player.duration
        val newPosition = (player.currentPosition + offsetMs).coerceIn(0, duration)
        player.seekTo(newPosition)
    }

    fun setShuffle(enabled: Boolean) {
        exoPlayer?.shuffleModeEnabled = enabled
    }

    fun setRepeatMode(mode: RepeatMode) {
        exoPlayer?.repeatMode = when (mode) {
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
        }
    }

    fun next() {
        exoPlayer?.seekToNext()
    }

    fun previous() {
        exoPlayer?.seekToPrevious()
    }

    fun setSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
    }

    @OptIn(UnstableApi::class)
    fun setPitch(pitch: Float) {
        exoPlayer?.setPlaybackParameters(
            exoPlayer?.playbackParameters?.withPitch(pitch) ?: PlaybackParameters(
                1f,
                pitch
            )
        )
    }

    fun handleEvent(event: PlaybackEvent) {
        when (event) {
            is PlaybackEvent.Play -> resume()
            is PlaybackEvent.Pause -> pause()
            is PlaybackEvent.Next -> next()
            is PlaybackEvent.Previous -> previous()
            is PlaybackEvent.SeekTo -> seekTo(event.position)
            is PlaybackEvent.SetShuffle -> setShuffle(event.on)
            is PlaybackEvent.SetRepeatMode -> setRepeatMode(event.mode)
            is PlaybackEvent.SetSpeed -> setSpeed(event.speed)
            is PlaybackEvent.SetPitch -> setPitch(event.pitch)
            is PlaybackEvent.SetQueue -> {
                setQueue(event.songs, event.startIndex)
                resume()
            }
            is PlaybackEvent.RemoveFromQueue -> removeFromQueue(event.songId)
            is PlaybackEvent.MoveQueueItem -> moveQueueItem(event.from, event.to)
            is PlaybackEvent.AddSongsToQueue -> addSongsToQueue(event.songs)
            else -> { /* Other complex events handled at Repository level */ }
        }
    }

    fun removeFromQueue(songId: Long) {
        val player = exoPlayer ?: return
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == songId.toString()) {
                player.removeMediaItem(i)
                break
            }
        }
    }

    fun moveQueueItem(from: Int, to: Int) {
        exoPlayer?.moveMediaItem(from, to)
    }

    fun addSongsToQueue(songs: List<Song>) {
        val player = getOrCreatePlayer()
        player.addMediaItems(MediaItemMapper.toMediaItems(songs))
    }

    fun setQueue(songs: List<Song>, startIndex: Int = 0, startPosition: Long = 0) {
        val player = getOrCreatePlayer()
        player.setMediaItems(MediaItemMapper.toMediaItems(songs), startIndex, startPosition)
        player.prepare()
    }

    fun release() {
        exoPlayer?.let {
            it.removeListener(playerListener)
            it.release()
        }
        exoPlayer = null
    }

    /**
     * Gọi khi Service chuẩn bị kết thúc để thông báo cho các listener lưu lại lần cuối.
     */
    fun onSessionEnded() {
        val player = exoPlayer ?: return
        val queueIds = PlaybackStateMapper.getQueueIds(player)
        val currentMediaItem = player.currentMediaItem
        val songId = currentMediaItem?.mediaId?.toLongOrNull()
        val position = player.currentPosition

        scope.launch {
            listeners.forEach { it.onSessionEnded(songId, position, queueIds) }
        }
    }
}
