package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringOptionTest : AbstractActionTest() {
    @Test
    fun `can define string option`() {
        class StringOption : Action() {
            val option by option("o")
        }

        parse(StringOption(), listOf("--o", "123")) { action ->
            assertEquals("123", action.option)
        }
    }

    @Test
    fun `value is null when option not provided`() {
        class StringOption : Action() {
            val option by option("o")
        }

        parse(StringOption(), emptyList()) { action ->
            assertNull(action.option)
        }
    }

    @Test
    fun `can provide default value for string option`() {
        class StringOption : Action() {
            val option by option("o").default("value")
        }

        parse(StringOption(), emptyList()) { action ->
            assertEquals("value", action.option)
        }

        parse(StringOption(), listOf("--o", "123")) { action ->
            assertEquals("123", action.option)
        }
    }

    @Test
    fun `can define multiple string options`() {
        class StringOption : Action() {
            val o1 by option("o1")
            val o2 by option("o2")
        }

        parse(StringOption(), emptyList()) { action ->
            assertNull(action.o1)
            assertNull(action.o2)
        }

        parse(StringOption(), listOf("--o1", "123")) { action ->
            assertEquals("123", action.o1)
            assertNull(action.o2)
        }

        parse(StringOption(), listOf("--o2", "123")) { action ->
            assertNull(action.o1)
            assertEquals("123", action.o2)
        }

        parse(StringOption(), listOf("--o2", "123", "--o1", "456")) { action ->
            assertEquals("456", action.o1)
            assertEquals("123", action.o2)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class StringOption : Action() {
            val option by option("o")
        }

        parseFails(StringOption(), listOf("--o"), "Argument missing for option --o")
    }
}