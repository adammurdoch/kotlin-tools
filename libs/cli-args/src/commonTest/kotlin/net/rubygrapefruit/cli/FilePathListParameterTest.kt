package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathListParameterTest : AbstractActionTest() {
    @Test
    fun `action can have parameter with list value`() {
        class Parameter : Action() {
            val param by parameters("value").path()
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(Parameter(), listOf("abc")) { action ->
            assertEquals(listOf(FilePath("abc")), action.param)
        }
        parse(Parameter(), listOf("a", "b", "c")) { action ->
            assertEquals(listOf(FilePath("a"), FilePath("b"), FilePath("c")), action.param)
        }
    }

}