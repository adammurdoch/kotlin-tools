package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.fail

class CustomTypeOptionTest : AbstractActionTest() {
    @Test
    fun `can define custom type option with long name`() {
        class Option : Action() {
            val option by type { ConversionResult.Success(it.uppercase()) }.option("opt")
        }

        parse(Option(), listOf("--opt", "abc")) { action ->
            assertEquals("ABC", action.option)
        }

        parseFails(::Option, listOf("-opt"), "Unknown option: -opt")
    }

    @Test
    fun `fails when argument cannot be converted`() {
        class Option : Action() {
            val option by type {
                if (it.length <= 1) {
                    ConversionResult.Failure("is too short")
                } else {
                    ConversionResult.Success(it)
                }
            }.option("o")
        }

        parseFails(::Option, listOf("-o", "a"), "Value for option -o is too short: a")
        parse(Option(), listOf("-o", "abc")) { action ->
            assertEquals("abc", action.option)
        }
    }

    @Test
    fun `fails when conversion throws exception`() {
        val failure = RuntimeException("broken")

        class Option : Action() {
            val option by type<Long> {
                throw failure
            }.option("o")
        }

        try {
            Option().parse(listOf("-o", "a"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Could not convert value for option -o: a", e.message)
            assertSame(failure, e.cause)
        }
    }
}