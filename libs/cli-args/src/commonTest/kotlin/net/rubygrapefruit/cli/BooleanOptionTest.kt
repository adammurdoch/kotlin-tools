package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BooleanOptionTest : AbstractActionTest() {
    @Test
    fun `can define boolean option with long name`() {
        class Option : Action() {
            val option by boolean().option("opt")
        }

        parse(Option(), listOf("--opt", "yes")) { action ->
            assertEquals(true, action.option)
        }
        parse(Option(), listOf("--opt", "no")) { action ->
            assertEquals(false, action.option)
        }

        parseFails(::Option, listOf("-opt"), "Unknown option: -opt")
    }

    @Test
    fun `can define int option with short name`() {
        class Option : Action() {
            val option by boolean().option("o")
        }

        parse(Option(), listOf("-o", "yes")) { action ->
            assertEquals(true, action.option)
        }
        parse(Option(), listOf("-o", "no")) { action ->
            assertEquals(false, action.option)
        }

        parseFails(::Option, listOf("--o"), "Unknown option: --o")
    }

    @Test
    fun `value is null when option not provided`() {
        class Option : Action() {
            val option by boolean().option("opt")
        }

        parse(Option(), emptyList()) { action ->
            assertNull(action.option)
        }
    }

    @Test
    fun `fails when argument is not a boolean`() {
        class Option : Action() {
            val option by boolean().option("o")
        }

        parseFails(::Option, listOf("-o", "true"), "Unknown value for option -o: true")
    }
}