package com.example.myapplication

import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestComponent @Inject constructor() {
    fun someFunction() {
        logcat { "testComponent: someFunction()" }
    }
}
