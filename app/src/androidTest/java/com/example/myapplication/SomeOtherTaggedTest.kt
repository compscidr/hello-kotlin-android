package com.example.myapplication

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class SomeOtherTaggedTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Tag("OTHERTEST")
    @Test
    fun test() {
        logger.debug("SomeOtherTaggedTest")
    }
}
