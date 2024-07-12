package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceParameterTest : AbstractActionTest() {
    @Test
    fun `action can have choice parameter`() {
        class Parameter : Action() {
            val param by oneOf {
                choice(1, "one")
                choice(2, "2")
            }.parameter("value")
        }

        parse(::Parameter, listOf("one")) { action ->
            assertEquals(1, action.param)
        }
        parse(::Parameter, listOf("2")) { action ->
            assertEquals(2, action.param)
        }
    }

    @Test
    fun `fails when argument is not one of the choices`() {
        class Parameter : Action() {
            val param by oneOf {
                choice(1, "one")
                choice(2, "2")
            }.parameter("value")
        }

        parseFails(::Parameter, listOf("abc"), "Unknown value for parameter 'value': abc")
    }

    @Test
    fun `fails when argument not provided`() {
        class Parameter : Action() {
            val param by oneOf {
                choice(1, "one")
                choice(2, "2")
            }.parameter("value")
        }

        parseFails(::Parameter, emptyList(), "Parameter 'value' not provided")
    }

    @Test
    fun `can provide default value for parameter`() {
        class Parameter : Action() {
            val param by oneOf {
                choice(1, "one")
                choice(2, "2")
            }.parameter("value").whenAbsent(12)
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(12, action.param)
        }

        parse(Parameter(), listOf("2")) { action ->
            assertEquals(2, action.param)
        }
    }

    @Test
    fun `can chain optional choices`() {
        class Parameter : Action() {
            val p1 by oneOf {
                choice(1, "one")
                choice(2, "2")
            }.parameter("value").whenAbsent(3)
            val p2 by oneOf {
                choice(1, "1st")
                choice(2, "2nd")
            }.parameter("value").whenAbsent(3)
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals(3, action.p1)
            assertEquals(3, action.p2)
        }
        parse(::Parameter, listOf("one")) { action ->
            assertEquals(1, action.p1)
            assertEquals(3, action.p2)
        }
        parse(::Parameter, listOf("2nd")) { action ->
            assertEquals(3, action.p1)
            assertEquals(2, action.p2)
        }
        parse(::Parameter, listOf("one", "2nd")) { action ->
            assertEquals(1, action.p1)
            assertEquals(2, action.p2)
        }
    }
}