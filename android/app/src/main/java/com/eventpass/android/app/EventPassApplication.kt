package com.eventpass.android.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * EventPass Application class.
 * Entry point for Hilt dependency injection.
 */
@HiltAndroidApp
class EventPassApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide configurations here
    }
}
