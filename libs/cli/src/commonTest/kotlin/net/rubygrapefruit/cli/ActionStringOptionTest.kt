package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class ActionStringOptionTest {
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