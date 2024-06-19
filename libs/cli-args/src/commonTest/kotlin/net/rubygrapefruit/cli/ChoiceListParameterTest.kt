package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceListParameterTest : AbstractActionTest() {
    @Test
    fun `action can have parameter with list value`() {
        class Parameter : Action() {
            val param by oneOf {
                choice(1, "1")
                choice(2, "two")
            }.parameters("value")
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(Parameter(), listOf("1")) { action ->
            assertEquals(listOf(1), action.param)
        }
        parse(Parameter(), listOf("1", "two", "1")) { action ->
            assertEquals(listOf(1, 2, 1), action.param)
        }
    }

    @Test
    fun `fails when argument is not an integer`() {
        class Parameter : Action() {
            val param by oneOf {
                choice(1, "1")
                choice(2, "two")
            }.parameters("value")
        }

        parseFails(Parameter(), listOf("3"), "Unknown value for parameter 'value': 3")
        parseFails(Parameter(), listOf("1", "3"), "Unknown value for parameter 'value': 3")
    }
}