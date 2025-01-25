package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class StringParameterAndListParameterTest : AbstractActionTest() {
    @Test
    fun `can consume remaining args`() {
        class Parameter : Action() {
            val param by parameter("param")
            val params by remainder("value")
        }

        parse(::Parameter, listOf("a")) { action ->
            assertEquals("a", action.param)
            assertEquals(emptyList(), action.params)
        }
        parse(::Parameter, listOf("a", "b")) { action ->
            assertEquals("a", action.param)
            assertEquals(listOf("b"), action.params)
        }
        parse(::Parameter, listOf("a", "--flag")) { action ->
            assertEquals("a", action.param)
            assertEquals(listOf("--flag"), action.params)
        }

        parseFails(::Parameter, emptyList(), "Parameter 'param' not provided")
        parseFails(::Parameter, listOf("--flag", "a"), "Unknown option: --flag")
    }
}