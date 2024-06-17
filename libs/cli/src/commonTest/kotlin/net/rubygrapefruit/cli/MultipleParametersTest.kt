package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class MultipleParametersTest : AbstractActionTest() {
    @Test
    fun `action can have parameter with list value`() {
        class Parameter : Action() {
            val param by parameters("value")
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(Parameter(), listOf("abc")) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(Parameter(), listOf("a", "b", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.param)
        }
    }

    @Test
    fun `can define a default value`() {
        class Parameter : Action() {
            val param by parameters("value").whenAbsent(listOf("abc"))
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(Parameter(), listOf("abc")) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(Parameter(), listOf("a", "b", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.param)
        }
    }

    @Test
    fun `fails when unknown flag provided instead of argument`() {
        class StringOption : Action() {
            val param by parameters("value")
        }

        parseFails(StringOption(), listOf("--flag"), "Unknown option: --flag")
        parseFails(StringOption(), listOf("arg", "--flag"), "Unknown option: --flag")
        parseFails(StringOption(), listOf("--flag", "arg"), "Unknown option: --flag")
    }
}