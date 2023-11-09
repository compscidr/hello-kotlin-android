package com.example.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.slf4j.LoggerFactory

@HiltAndroidApp
class HelloApp : Application() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onCreate() {
        super.onCreate()
        logger.debug("HelloApp: onCreate()")
    }
}
