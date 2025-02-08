package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class StringOptionTest : AbstractActionTest() {
    @Test
    fun `can define string option with long name`() {
        class Option : Action() {
            val option by option("opt")
        }

        parse(Option(), listOf("--opt", "123")) { action ->
            assertEquals("123", action.option)
        }

        parseFails(::Option, listOf("-opt", "123"), "Unknown option: -opt")
    }

    @Test
    fun `can define string option with short name`() {
        class Option : Action() {
            val option by option("o")
        }

        parse(Option(), listOf("-o", "123")) { action ->
            assertEquals("123", action.option)
        }

        parseFails(::Option, listOf("--o", "123"), "Unknown option: --o")
    }

    @Test
    fun `can define option with multiple names`() {
        class Option : Action() {
            val option by option("o", "opt")
        }

        parse(Option(), listOf("-o", "123")) { action ->
            assertEquals("123", action.option)
        }
        parse(Option(), listOf("--opt", "123")) { action ->
            assertEquals("123", action.option)
        }
    }

    @Test
    fun `value is null when option not provided`() {
        class Option : Action() {
            val option by option("o")
        }

        parse(Option(), emptyList()) { action ->
            assertNull(action.option)
        }
    }

    @Test
    fun `can provide default value for option`() {
        class Option : Action() {
            val option by option("opt").whenAbsent("value")
        }

        parse(Option(), emptyList()) { action ->
            assertEquals("value", action.option)
        }

        parse(Option(), listOf("--opt", "123")) { action ->
            assertEquals("123", action.option)
        }
    }

    @Test
    fun `can define multiple options`() {
        class Option : Action() {
            val o1 by option("o1")
            val o2 by option("o2")
        }

        parse(Option(), emptyList()) { action ->
            assertNull(action.o1)
            assertNull(action.o2)
        }

        parse(Option(), listOf("--o1", "123")) { action ->
            assertEquals("123", action.o1)
            assertNull(action.o2)
        }

        parse(Option(), listOf("--o2", "123")) { action ->
            assertNull(action.o1)
            assertEquals("123", action.o2)
        }

        parse(Option(), listOf("--o2", "123", "--o1", "456")) { action ->
            assertEquals("456", action.o1)
            assertEquals("123", action.o2)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class Option : Action() {
            val option by option("o")
        }

        parseFails(::Option, listOf("-o"), "Value missing for option -o")
    }

    @Test
    fun `fails when multiple options and argument not provided`() {
        class Option : Action() {
            val option1 by option("o1")
            val option2 by option("o2")
        }

        parseFails(::Option, listOf("--o1"), "Value missing for option --o1")
        parseFails(::Option, listOf("--o1", "a", "--o2"), "Value missing for option --o2")
        parseFails(::Option, listOf("--o2", "a", "--o1"), "Value missing for option --o1")
        parseFails(::Option, listOf("--o1", "--o2"), "Value missing for option --o1")
    }

    @Test
    fun `can require option to be present`() {
        class Option : Action() {
            val option by option("o", "long-option").required()
        }

        parse(Option(), listOf("-o", "value")) { action ->
            assertEquals("value", action.option)
        }
        parseFails(::Option, emptyList(), "Option --long-option not provided")
    }

    @Test
    fun `can require multiple options to be present`() {
        class Option : Action() {
            val option1 by option("option-1").required()
            val option2 by option("option-2").required()
        }

        parse(Option(), listOf("--option-1", "value 1", "--option-2", "value 2")) { action ->
            assertEquals("value 1", action.option1)
            assertEquals("value 2", action.option2)
        }
        parse(Option(), listOf("--option-2", "value 2", "--option-1", "value 1")) { action ->
            assertEquals("value 1", action.option1)
            assertEquals("value 2", action.option2)
        }
        parseFails(::Option, emptyList(), "Option --option-1 not provided")
        parseFails(::Option, listOf("--option-1", "value 1"), "Option --option-2 not provided")
    }

    @Test
    fun `fails when flag provided instead of argument`() {
        class Option : Action() {
            val option by option("o")
            val other by flag("f", "flag")
        }

        parseFails(::Option, listOf("-o", "-f"), "Value missing for option -o")
        parseFails(::Option, listOf("-o", "--flag"), "Value missing for option -o")
        parseFails(::Option, listOf("-o", "-u"), "Unknown option: -u")
        parseFails(::Option, listOf("-o", "--unknown"), "Unknown option: --unknown")
    }

    @Test
    fun `fails when unknown flag used with option`() {
        class Option : Action() {
            val option by option("o")
        }

        parseFails(::Option, listOf("--flag", "-o", "arg"), "Unknown option: --flag")
        parseFails(::Option, listOf("-o", "arg", "--flag"), "Unknown option: --flag")
        parseFails(::Option, listOf("-o", "--flag", "arg"), "Unknown option: --flag")
    }

    @Test
    fun `fails when option is present multiple times`() {
        class Option : Action() {
            val option by option("o", "long")
        }

        parseFails(::Option, listOf("-o", "1", "-o", "2"), "Value for option -o already provided")
        parseFails(::Option, listOf("-o", "1", "-o"), "Value for option -o already provided")
        parseFails(::Option, listOf("--long", "1", "-o", "2"), "Value for option -o already provided")
        parseFails(::Option, listOf("-o", "1", "--long", "2"), "Value for option --long already provided")
    }

    @Test
    fun `fails when multiple options and option is present multiple times`() {
        class Option : Action() {
            val option1 by option("o")
            val option2 by option("p")
        }

        println("-> START TEST")
        parseFails(::Option, listOf("-o", "1", "-o", "2", "-p", "3"), "Value for option -o already provided")
        parseFails(::Option, listOf("-o", "1", "-p", "2", "-o", "3"), "Value for option -o already provided")
    }

    @Test
    fun `option name must not start with punctuation`() {
        try {
            Action().option("-o")
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-o cannot be used as an option name", e.message)
        }
        try {
            Action().option("o", "--option")
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--option cannot be used as an option name", e.message)
        }
    }

    @Test
    fun `option names must be unique`() {
        val action = Action()
        action.option("o", "option")
        try {
            action.option("o")
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-o is used by another option", e.message)
        }
    }
}