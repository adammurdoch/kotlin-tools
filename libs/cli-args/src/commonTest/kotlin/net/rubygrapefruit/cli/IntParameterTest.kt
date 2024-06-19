package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class IntParameterTest : AbstractActionTest() {
    @Test
    fun `action can have int parameter`() {
        class Parameter : Action() {
            val param by int().parameter("value")
        }

        parse(Parameter(), listOf("123")) { action ->
            assertEquals(123, action.param)
        }
    }

    @Test
    fun `fails when argument is not an integer`() {
        class Parameter : Action() {
            val param by int().parameter("value")
        }

        parseFails(Parameter(), listOf("abc"), "Value for parameter 'value' is not an integer: abc")
    }

    @Test
    fun `fails when argument not provided`() {
        class Parameter : Action() {
            val param by int().parameter("value")
        }

        parseFails(Parameter(), emptyList(), "Parameter 'value' not provided")
    }

    @Test
    fun `can provide default value for parameter`() {
        class Parameter : Action() {
            val param by int().parameter("value").whenAbsent(12)
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(12, action.param)
        }

        parse(Parameter(), listOf("123")) { action ->
            assertEquals(123, action.param)
        }
    }
}