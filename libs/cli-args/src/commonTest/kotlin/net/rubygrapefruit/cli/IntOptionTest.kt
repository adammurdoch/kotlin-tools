package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IntOptionTest : AbstractActionTest() {
    @Test
    fun `can define int option with long name`() {
        class Option : Action() {
            val option by int().option("opt")
        }

        parse(Option(), listOf("--opt", "123")) { action ->
            assertEquals(123, action.option)
        }

        parseFails(::Option, listOf("-opt"), "Unknown option: -opt")
    }

    @Test
    fun `can define int option with short name`() {
        class Option : Action() {
            val option by int().option("o")
        }

        parse(Option(), listOf("-o", "123")) { action ->
            assertEquals(123, action.option)
        }

        parseFails(::Option, listOf("--o"), "Unknown option: --o")
    }

    @Test
    fun `value is null when option not provided`() {
        class Option : Action() {
            val option by int().option("opt")
        }

        parse(Option(), emptyList()) { action ->
            assertNull(action.option)
        }
    }

    @Test
    fun `can provide default value for option`() {
        class Option : Action() {
            val option by int().option("opt").whenAbsent(45)
        }

        parse(Option(), emptyList()) { action ->
            assertEquals(45, action.option)
        }

        parse(Option(), listOf("--opt", "123")) { action ->
            assertEquals(123, action.option)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class Option : Action() {
            val option by int().option("o")
        }

        parseFails(::Option, listOf("-o"), "Value missing for option -o")
    }

    @Test
    fun `fails when argument is not an integer`() {
        class Option : Action() {
            val option by int().option("o")
        }

        parseFails(::Option, listOf("-o", "abc"), "Value for option -o is not an integer: abc")
    }

    @Test
    fun `fails when option is present multiple times and second argument is badly formed`() {
        class Option : Action() {
            val option by int().option("o")
        }

        parseFails(::Option, listOf("-o", "123", "-o", "abc"), "Value for option -o already provided")
        parseFails(::Option, listOf("-o", "123", "-o"), "Value for option -o already provided")
    }

    @Test
    fun `fails when flag used and argument is not an integer`() {
        class Option : Action() {
            val option by int().option("o")
            val other by flag("f", "flag")
        }

        parseFails(::Option, listOf("-o", "abc", "--flag"), "Value for option -o is not an integer: abc")
        parseFails(::Option, listOf("-o", "abc", "--unknown"), "Value for option -o is not an integer: abc")
    }
}