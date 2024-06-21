package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringListParameterTest : AbstractActionTest() {
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
    fun `can require at least one argument`() {
        class Parameter : Action() {
            val param by parameters("value").required()
        }

        parseFails(Parameter(), emptyList(), "Parameter 'value' not provided")

        parse(Parameter(), listOf("abc")) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(Parameter(), listOf("a", "b", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.param)
        }
    }

    @Test
    fun `fails when unknown flag provided instead of argument`() {
        class Parameter : Action() {
            val param by parameters("value")
        }

        parseFails(Parameter(), listOf("--flag"), "Unknown option: --flag")
        parseFails(Parameter(), listOf("arg", "--flag"), "Unknown option: --flag")
        parseFails(Parameter(), listOf("--flag", "arg"), "Unknown option: --flag")
    }

    @Test
    fun `can consume remaining args`() {
        class Parameter : Action() {
            val param by string().parameters("value", acceptOptions = true)
            val flag by flag("flag")
        }

        parse(Parameter(), listOf("--flag")) { action ->
            assertEquals(emptyList(), action.param)
            assertTrue(action.flag)
        }
        parse(Parameter(), listOf("--other")) { action ->
            assertEquals(listOf("--other"), action.param)
        }
        parse(Parameter(), listOf("a", "--flag", "b", "--other")) { action ->
            assertEquals(listOf("a", "--flag", "b", "--other"), action.param)
            assertFalse(action.flag)
        }
    }
}