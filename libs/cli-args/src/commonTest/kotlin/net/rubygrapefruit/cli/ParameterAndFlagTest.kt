package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParameterAndFlagTest : AbstractActionTest() {
    @Test
    fun `can combine flag and parameter`() {
        class MixedAction : Action() {
            val flag by flag("flag")
            val param by parameter("param")
        }

        parse(MixedAction(), listOf("--flag", "abc")) { action ->
            assertTrue(action.flag)
            assertEquals("abc", action.param)
        }

        parse(MixedAction(), listOf("abc", "--flag")) { action ->
            assertTrue(action.flag)
            assertEquals("abc", action.param)
        }

        parse(MixedAction(), listOf("abc")) { action ->
            assertFalse(action.flag)
            assertEquals("abc", action.param)
        }
    }
}