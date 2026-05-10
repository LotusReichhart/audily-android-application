package com.lotusreichhart.audily

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.lotusreichhart.audily.feature.songs.impl.menu.SongMenuInitializer

@HiltAndroidApp
class AudilyApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("AudilyApplication onCreate....")
        
        // Initialize global components
        SongMenuInitializer.initialize()
    }
}