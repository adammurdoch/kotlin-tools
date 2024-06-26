package net.rubygrapefruit.cli

import kotlin.test.*

class StringOptionTest : AbstractActionTest() {
    @Test
    fun `can define string option with long name`() {
        class Option : Action() {
            val option by option("opt")
        }

        parse(Option(), listOf("--opt", "123")) { action ->
            assertEquals("123", action.option)
        }

        parseFails(Option(), listOf("-opt", "123"), "Unknown option: -opt")
    }

    @Test
    fun `can define string option with short name`() {
        class Option : Action() {
            val option by option("o")
        }

        parse(Option(), listOf("-o", "123")) { action ->
            assertEquals("123", action.option)
        }

        parseFails(Option(), listOf("--o", "123"), "Unknown option: --o")
    }

    @Test
    fun `can define string option with multiple names`() {
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
    fun `can provide default value for string option`() {
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
    fun `can define multiple string options`() {
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

        parseFails(Option(), listOf("-o"), "Value missing for option -o")
    }

    @Test
    fun `fails when flag provided instead of argument`() {
        class Option : Action() {
            val option by option("o")
            val other by flag("f", "flag")
        }

        parseFails(Option(), listOf("-o", "-f"), "Value missing for option -o")
        parseFails(Option(), listOf("-o", "--flag"), "Value missing for option -o")
        parseFails(Option(), listOf("-o", "-u"), "Value missing for option -o")
        parseFails(Option(), listOf("-o", "--unknown"), "Value missing for option -o")
    }

    @Test
    fun `reports unknown flag used with option`() {
        class Option : Action() {
            val option by option("o")
        }

        parseFails(Option(), listOf("--flag", "-o", "arg"), "Unknown option: --flag")
        parseFails(Option(), listOf("-o", "arg", "--flag"), "Unknown option: --flag")
        parseFails(Option(), listOf("-o", "--flag", "arg"), "Value missing for option -o")
    }

    @Test
    fun `fails when option is present multiple times`() {
        class Option : Action() {
            val option by option("o")
        }

        parseFails(Option(), listOf("-o", "1", "-o", "2"), "Value for option -o already provided")
    }

    @Test
    fun `option name must not start with punctuation`() {
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

    @Test
    fun `can run --help command without providing argument`() {
        class Option : TestApp("cmd") {
            val param by option("value")
        }

        parseRecovers(Option(), listOf("--value", "--help")) { action ->
            assertTrue(action.help)
        }
    }
}