package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathParameterTest : AbstractActionTest() {
    @Test
    fun `action can have file path parameter`() {
        class Parameter : Action() {
            val param by path().parameter("value")
        }

        parse(Parameter(), listOf("a/b")) { action ->
            assertEquals(FilePath("a/b"), action.param)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class Parameter : Action() {
            val param by path().parameter("value")
        }

        parseFails(Parameter(), emptyList(), "Parameter 'value' not provided")
    }
}