package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceParameterTest : AbstractActionTest() {
    @Test
    fun `action can have choice parameter`() {
        class Parameter : Action() {
            val param by parameter("value").oneOf {
                choice(1, "one")
                choice(2, "2")
            }
        }

        parse(Parameter(), listOf("one")) { action ->
            assertEquals(1, action.param)
        }
        parse(Parameter(), listOf("2")) { action ->
            assertEquals(2, action.param)
        }
    }

    @Test
    fun `fails when argument is not one of the choices`() {
        class Parameter : Action() {
            val param by parameter("value").oneOf {
                choice(1, "one")
                choice(2, "2")
            }
        }

        parseFails(Parameter(), listOf("abc"), "Unknown value for parameter 'value': abc")
    }

    @Test
    fun `fails when argument not provided`() {
        class Parameter : Action() {
            val param by parameter("value").oneOf {
                choice(1, "one")
                choice(2, "2")
            }
        }

        parseFails(Parameter(), emptyList(), "Parameter 'value' not provided")
    }

    @Test
    fun `can provide default value for parameter`() {
        class Parameter : Action() {
            val param by parameter("value").oneOf {
                choice(1, "one")
                choice(2, "2")
            }.whenAbsent(12)
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(12, action.param)
        }

        parse(Parameter(), listOf("2")) { action ->
            assertEquals(2, action.param)
        }
    }
}