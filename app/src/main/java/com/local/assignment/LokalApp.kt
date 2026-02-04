package com.local.assignment

import android.app.Application
import timber.log.Timber

class LokalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
