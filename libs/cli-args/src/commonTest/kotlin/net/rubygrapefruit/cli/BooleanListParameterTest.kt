package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class BooleanListParameterTest : AbstractActionTest() {
    @Test
    fun `action can have parameter with list value`() {
        class Parameter : Action() {
            val param by boolean().parameter("value").repeated()
        }

        parse(::Parameter, listOf("yes")) { action ->
            assertEquals(listOf(true), action.param)
        }
        parse(::Parameter, listOf("yes", "no", "yes")) { action ->
            assertEquals(listOf(true, false, true), action.param)
        }
    }

    @Test
    fun `fails when argument is not a boolean`() {
        class Parameter : Action() {
            val param by boolean().parameter("value").repeated()
        }

        parseFails(::Parameter, listOf("abc"), "Unknown value for parameter 'value': abc")
        parseFails(::Parameter, listOf("yes", "abc"), "Unknown value for parameter 'value': abc")
    }
}