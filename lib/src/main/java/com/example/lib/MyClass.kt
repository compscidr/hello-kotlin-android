package com.example.lib

import org.slf4j.LoggerFactory

class MyClass {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun display() {
        logger.debug("In the display function!")
    }
}