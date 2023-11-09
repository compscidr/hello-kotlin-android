package com.example.myapplication

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HierarchicalTest {
    @Test
    fun fooTest() {
        val foo = Foo()
        foo.name = "Test"
        val bar = Bar()
        bar.nickname = "T"
        foo.bar = bar
        assertEquals("Test", foo.name)
        assertEquals("T", foo.bar.nickname)
        assertEquals("T", bar.nickname)
    }

    // https://www.baeldung.com/kotlin-mockk
    @Test
    fun givenHierarchicalClass_whenMockingIt_thenReturnProperValue() {
        // given
        val foo =
            mockk<Foo> {
                every { name } returns "Karol"
                every { bar } returns
                    mockk {
                        every { nickname } returns "Tomato"
                    }
            }

        // when
        val name = foo.name
        val nickname = foo.bar.nickname

        // then
        assertEquals("Karol", name)
        assertEquals("Tomato", nickname)
    }
}
