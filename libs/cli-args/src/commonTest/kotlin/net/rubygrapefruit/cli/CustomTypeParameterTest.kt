package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class CustomTypeParameterTest : AbstractActionTest() {
    @Test
    fun `action can have typed parameter`() {
        class Parameter : Action() {
            val param by type { ConversionResult.Success(it.uppercase()) }.parameter("value")
        }

        parse(Parameter(), listOf("abc")) { action ->
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

        parseFails(Parameter(), listOf("a"), "Value for parameter 'value' is too short: a")
        parse(Parameter(), listOf("abc")) { action ->
            assertEquals("abc", action.param)
        }
    }
}