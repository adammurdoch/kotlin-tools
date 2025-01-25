package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class IntListParameterTest : AbstractActionTest() {
    @Test
    fun `action can have parameter with list value`() {
        class Parameter : Action() {
            val param by int().parameter("value").repeated()
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(::Parameter, listOf("123")) { action ->
            assertEquals(listOf(123), action.param)
        }
        parse(::Parameter, listOf("1", "2", "3")) { action ->
            assertEquals(listOf(1, 2, 3), action.param)
        }
    }

    @Test
    fun `fails when argument is not an integer`() {
        class Parameter : Action() {
            val param by int().parameter("value").repeated()
        }

        parseFails(::Parameter, listOf("abc"), "Value for parameter 'value' is not an integer: abc")
        parseFails(::Parameter, listOf("1", "abc"), "Value for parameter 'value' is not an integer: abc")
    }
}