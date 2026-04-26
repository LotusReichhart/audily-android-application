package com.lotusreichhart.audily.core.playback

import android.content.Intent
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudilyAudioService : MediaLibraryService() {

    @Inject
    lateinit var playbackManager: PlaybackManager

    private var mediaSession: MediaLibraryService.MediaLibrarySession? = null

    override fun onCreate() {
        super.onCreate()

        val player = playbackManager.player
        mediaSession = MediaLibraryService.MediaLibrarySession.Builder(
            this,
            player,
            LibrarySessionCallback()
        ).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibraryService.MediaLibrarySession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        playbackManager.onSessionEnded()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        playbackManager.onSessionEnded()
        mediaSession?.let {
            it.player.release()
            it.release()
        }
        mediaSession = null
        playbackManager.release()
        super.onDestroy()
    }

    private inner class LibrarySessionCallback : MediaLibraryService.MediaLibrarySession.Callback {
        // có thể override các hàm xử lý lệnh tại đây nếu cần
    }
}
