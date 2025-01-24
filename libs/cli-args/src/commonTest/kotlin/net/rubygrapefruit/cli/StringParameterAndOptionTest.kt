package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class StringParameterAndOptionTest : AbstractActionTest() {
    @Test
    fun `can define parameter and required option`() {
        class Option : Action() {
            val option by option("opt").required()
            val param by parameter("param")
        }

        parse(Option(), listOf("--opt", "123", "abc")) { action ->
            assertEquals("123", action.option)
            assertEquals("abc", action.param)
        }
        parse(Option(), listOf("abc", "--opt", "123")) { action ->
            assertEquals("123", action.option)
            assertEquals("abc", action.param)
        }

        parseFails(::Option, listOf("123"), "Option --opt not provided")
        parseFails(::Option, listOf("--opt", "123"), "Parameter 'param' not provided")
    }
}