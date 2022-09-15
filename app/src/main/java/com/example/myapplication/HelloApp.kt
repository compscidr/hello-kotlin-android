package com.example.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.logcat

@HiltAndroidApp
class HelloApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this@HelloApp, minPriority = LogPriority.VERBOSE)
        logcat { "Logging installed " }
    }
}
