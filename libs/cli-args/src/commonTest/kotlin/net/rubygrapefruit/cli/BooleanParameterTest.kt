package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BooleanParameterTest : AbstractActionTest() {
    @Test
    fun `action can have boolean parameter`() {
        class Parameter : Action() {
            val param by boolean().parameter("value")
        }

        parse(Parameter(), listOf("yes")) { action ->
            assertTrue(action.param)
        }
        parse(Parameter(), listOf("no")) { action ->
            assertFalse(action.param)
        }
    }

    @Test
    fun `fails when argument is not an boolean`() {
        class Parameter : Action() {
            val param by boolean().parameter("value")
        }

        parseFails(Parameter(), listOf("abc"), "Unknown value for parameter 'value': abc")
    }
}