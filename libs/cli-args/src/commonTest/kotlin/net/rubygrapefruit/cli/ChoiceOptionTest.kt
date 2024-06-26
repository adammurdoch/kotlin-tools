package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ChoiceOptionTest : AbstractActionTest() {
    @Test
    fun `can define choice option with long name`() {
        class Option : Action() {
            val option by oneOf {
                choice(1, "1")
                choice(2, "two")
            }.option("opt")
        }

        parse(Option(), listOf("--opt", "1")) { action ->
            assertEquals(1, action.option)
        }
        parse(Option(), listOf("--opt", "two")) { action ->
            assertEquals(2, action.option)
        }

        parseFails(Option(), listOf("-opt"), "Unknown option: -opt")
    }

    @Test
    fun `can define choice option with short name`() {
        class Option : Action() {
            val option by oneOf {
                choice(1, "1")
                choice(2, "two")
            }.option("o")
        }

        parse(Option(), listOf("-o", "1")) { action ->
            assertEquals(1, action.option)
        }
        parse(Option(), listOf("-o", "two")) { action ->
            assertEquals(2, action.option)
        }

        parseFails(Option(), listOf("--o"), "Unknown option: --o")
    }

    @Test
    fun `fails when argument is not one of the choices`() {
        class Option : Action() {
            val option by oneOf {
                choice(1, "1")
                choice(2, "two")
            }.option("o")
        }

        parseFails(Option(), listOf("-o", "abc"), "Unknown value for option -o: abc")
    }

    @Test
    fun `name must not start with punctuation`() {
        class Option : Action() {
            val option by oneOf {
                choice(1, "1")
                choice(2, "--two")
            }.option("o")
        }
        try {
            Option()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--two cannot be used as a choice name", e.message)
        }
    }
}