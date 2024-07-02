package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

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
}