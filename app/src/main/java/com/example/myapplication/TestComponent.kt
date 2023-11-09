package com.example.myapplication

import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestComponent
    @Inject
    constructor() {
        private val logger = LoggerFactory.getLogger(javaClass)

        fun someFunction() {
            logger.debug("testComponent: someFunction()")
        }
    }
