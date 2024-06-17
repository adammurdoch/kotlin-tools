package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class StringOptionTest : AbstractActionTest() {
    @Test
    fun `can define string option with long name`() {
        class StringOption : Action() {
            val option by option("opt")
        }

        parse(StringOption(), listOf("--opt", "123")) { action ->
            assertEquals("123", action.option)
        }

        parseFails(StringOption(), listOf("-opt", "123"), "Unknown option: -opt")
    }

    @Test
    fun `can define string option with short name`() {
        class StringOption : Action() {
            val option by option("o")
        }

        parse(StringOption(), listOf("-o", "123")) { action ->
            assertEquals("123", action.option)
        }

        parseFails(StringOption(), listOf("--o", "123"), "Unknown option: --o")
    }

    @Test
    fun `can define string option with multiple names`() {
        class StringOption : Action() {
            val option by option("o", "opt")
        }

        parse(StringOption(), listOf("-o", "123")) { action ->
            assertEquals("123", action.option)
        }
        parse(StringOption(), listOf("--opt", "123")) { action ->
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
            val option by option("opt").whenAbsent("value")
        }

        parse(StringOption(), emptyList()) { action ->
            assertEquals("value", action.option)
        }

        parse(StringOption(), listOf("--opt", "123")) { action ->
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

        parseFails(StringOption(), listOf("-o"), "Value missing for option -o")
    }

    @Test
    fun `fails when unknown flag provided instead of argument`() {
        class StringOption : Action() {
            val option by option("o")
        }

        parseFails(StringOption(), listOf("-o", "--flag"), "Value missing for option -o")
        parseFails(StringOption(), listOf("--flag", "-o", "arg"), "Unknown option: --flag")
        parseFails(StringOption(), listOf("-o", "arg", "--flag"), "Unknown option: --flag")
    }

    @Test
    fun `fails when option is present multiple times`() {
        class StringOption : Action() {
            val option by option("o")
        }

        parseFails(StringOption(), listOf("-o", "1", "-o", "2"), "Value for option -o already provided")
    }

    @Test
    fun `name must not start with punctuation`() {
        class Broken1 : Action() {
            val option by option("-o")
        }
        try {
            Broken1()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-o cannot be used as an option name", e.message)
        }
        class Broken2 : Action() {
            val option by option("o", "--option")
        }
        try {
            Broken2()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--option cannot be used as an option name", e.message)
        }
    }
}