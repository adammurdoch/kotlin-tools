package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.fail

class CustomTypeListParameterTest : AbstractActionTest() {
    @Test
    fun `action can have typed parameter`() {
        class Parameter : Action() {
            val param by type { ConversionResult.Success(it.uppercase()) }.parameters("value")
        }

        parse(::Parameter, listOf("abc", "def")) { action ->
            assertEquals(listOf("ABC", "DEF"), action.param)
        }
    }

    @Test
    fun `fails when argument cannot be converted`() {
        class Parameter : Action() {
            val param by type {
                if (it.length <= 1) {
                    ConversionResult.Failure("is too short")
                } else {
                    ConversionResult.Success(it)
                }
            }.parameters("value")
        }

        parseFails(::Parameter, listOf("a"), "Value for parameter 'value' is too short: a")
        parseFails(::Parameter, listOf("abc", "d"), "Value for parameter 'value' is too short: d")

        parse(::Parameter, emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(::Parameter, listOf("abc")) { action ->
            assertEquals(listOf("abc"), action.param)
        }
    }

    @Test
    fun `fails when conversion throws exception`() {
        val failure = RuntimeException("broken")

        class Parameter : Action() {
            val param by type<Long> {
                throw failure
            }.parameters("value")
        }

        try {
            Parameter().parse(listOf("a"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Could not convert value for parameter 'value': a", e.message)
            assertSame(failure, e.cause)
        }
    }
}