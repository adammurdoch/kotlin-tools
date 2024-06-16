package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlagAndMultipleParametersTest : AbstractActionTest() {
    @Test
    fun `can interleave flags and arguments`() {
        class WithParams : Action() {
            val flag by flag("f1")
            val params by parameters("value")
        }

        parse(WithParams(), listOf("a", "--f1", "b")) { action ->
            assertEquals(listOf("a", "b"), action.params)
            assertTrue(action.flag)
        }
        parse(WithParams(), listOf("a", "--f1", "b", "--no-f1", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.params)
            assertTrue(action.flag)
        }
        parse(WithParams(), listOf("--f1")) { action ->
            assertEquals(emptyList(), action.params)
            assertTrue(action.flag)
        }
    }
}