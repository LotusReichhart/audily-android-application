package com.lotusreichhart.audily

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.lotusreichhart.audily.feature.songs.impl.menu.SongMenuInitializer

import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import javax.inject.Inject

@HiltAndroidApp
class AudilyApplication : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun newImageLoader(context: Context): ImageLoader {
        return imageLoader
    }
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