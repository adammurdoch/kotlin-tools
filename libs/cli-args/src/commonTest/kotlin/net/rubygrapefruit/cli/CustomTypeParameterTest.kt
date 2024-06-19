package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class CustomTypeParameterTest : AbstractActionTest() {
    @Test
    fun `action can have typed parameter`() {
        class Parameter : Action() {
            val param by type { it.uppercase() }.parameter("value")
        }

        parse(Parameter(), listOf("abc")) { action ->
            assertEquals("ABC", action.param)
        }
    }

    @Test
    fun `fails when argument cannot be converted`() {
        class Parameter : Action() {
            val param by type { if (it.length <= 1) null else it }.parameter("value")
        }

        parseFails(Parameter(), listOf("a"), "Unknown value for parameter 'value': a")
        parse(Parameter(), listOf("abc")) { action ->
            assertEquals("abc", action.param)
        }
    }
}