package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.fail

class CustomTypeParameterTest : AbstractActionTest() {
    @Test
    fun `action can have typed parameter`() {
        class Parameter : Action() {
            val param by type { ConversionResult.Success(it.uppercase()) }.parameter("value")
        }

        parse(::Parameter, listOf("abc")) { action ->
            assertEquals("ABC", action.param)
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
            }.parameter("value")
        }

        parseFails(::Parameter, listOf("a"), "Value for parameter 'value' is too short: a")
        parse(::Parameter, listOf("abc")) { action ->
            assertEquals("abc", action.param)
        }
    }

    @Test
    fun `fails when conversion throws exception`() {
        val failure = RuntimeException("broken")

        class Parameter : Action() {
            val param by type<Long> {
                throw failure
            }.parameter("value")
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