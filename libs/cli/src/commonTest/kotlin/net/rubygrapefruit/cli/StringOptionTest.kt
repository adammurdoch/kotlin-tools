package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class StringOptionTest {
    @Test
    fun `can define string option`() {
        class StringOption : Action() {
            val option by option("o")
        }

        val action = StringOption()
        action.parse(listOf("--o", "123"))

        assertEquals("123", action.option)
    }

    @Test
    fun `value is null when option not provided`() {
        class StringOption : Action() {
            val option by option("o")
        }

        val action = StringOption()
        action.parse(emptyList())

        assertNull(action.option)
    }

    @Test
    fun `can define multiple string options`() {
        class StringOption : Action() {
            val o1 by option("o1")
            val o2 by option("o2")
        }

        val action = StringOption()
        action.parse(listOf("--o1", "123"))

        assertEquals("123", action.o1)
        assertNull(action.o2)

        val action2 = StringOption()
        action2.parse(listOf("--o2", "123"))

        assertNull(action2.o1)
        assertEquals("123", action2.o2)
    }

    @Test
    fun `fails when argument not provided`() {
        class StringOption : Action() {
            val option by option("o")
        }

        val action = StringOption()
        try {
            action.parse(listOf("--o"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Argument missing for option --o", e.message)
        }
    }
}