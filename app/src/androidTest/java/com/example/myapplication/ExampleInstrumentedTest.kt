package com.example.myapplication

import androidx.test.platform.app.InstrumentationRegistry
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.LoggerFactory

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@Timeout(30)
class ExampleInstrumentedTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    @JvmField
    @RegisterExtension
    var scenarioExtension = ActivityScenarioExtension.launch(
        MainActivity::class.java
    )

    // @Tag("TEST1") // tags broken: https://github.com/mannodermaus/android-junit5/issues/298
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.myapplication", appContext.packageName)

        val scenario = scenarioExtension.scenario
        scenario.onActivity { }
    }

    // @Tag("TEST2")
    @Test
    fun test2() {
        logger.debug("test2")
    }

    // @Tag("TEST3")
    @Test
    fun test3() {
        logger.debug("test3")
    }
}
