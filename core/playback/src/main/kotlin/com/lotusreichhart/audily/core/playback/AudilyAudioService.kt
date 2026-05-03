package com.lotusreichhart.audily.core.playback

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AudilyAudioService : MediaLibraryService() {

    @Inject
    lateinit var playbackManager: PlaybackManager

    companion object {
        const val ACTION_OPEN_PLAYER = "com.lotusreichhart.audily.action.OPEN_PLAYER"
    }

    private var mediaSession: MediaLibrarySession? = null

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        Timber.d("AudilyAudioService Created")

        // Tạo Intent để mở app và bung màn hình NowPlaying
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            action = ACTION_OPEN_PLAYER
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val player = playbackManager.player

        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            LibrarySessionCallback()
        )
            .setSessionActivity(pendingIntent)
            .build()

        // QUAN TRỌNG: Phải addSession thì Media3 mới tự động hiển thị Notification
        mediaSession?.let { addSession(it) }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = playbackManager.player
        if (!player.playWhenReady || player.playbackState == androidx.media3.common.Player.STATE_IDLE) {
            playbackManager.onSessionEnded()
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Timber.d("AudilyAudioService Destroyed")
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {
        // Media3 tự động xử lý các lệnh Player mặc định (Play/Pause/Next/Prev/Seek)
    }
}
