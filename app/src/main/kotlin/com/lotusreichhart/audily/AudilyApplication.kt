package com.lotusreichhart.audily

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AudilyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.d("AudilyApplication onCreate....")
    }
}