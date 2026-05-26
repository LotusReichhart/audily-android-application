package com.lotusreichhart.audily.core.playback

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import coil3.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AudilyAudioService : MediaLibraryService() {

    @Inject
    lateinit var playbackManager: PlaybackManager

    @Inject
    lateinit var imageLoader: ImageLoader

    companion object {
        const val ACTION_OPEN_PLAYER = "com.lotusreichhart.audily.action.OPEN_PLAYER"
    }

    private var mediaSession: MediaLibrarySession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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
            .setBitmapLoader(
                AudilyBitmapLoader(
                    context = this,
                    imageLoader = imageLoader,
                    scope = serviceScope
                )
            )
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
        Timber.d("Audily Service Kill - Destroyed")
        serviceScope.cancel() // Hủy bỏ coroutines đang chạy để nạp ảnh bìa
        playbackManager.onSessionEnded() // Lưu lại trạng thái cuối cùng trước khi hủy hoàn toàn
        mediaSession?.release()
        mediaSession = null
        playbackManager.release() // Đảm bảo ExoPlayer được giải phóng hoàn toàn
        super.onDestroy()
    }

    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {
        // Media3 tự động xử lý các lệnh Player mặc định (Play/Pause/Next/Prev/Seek)
    }
}
