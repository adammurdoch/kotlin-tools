package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArgumentAndFlagTest : AbstractActionTest() {
    @Test
    fun `can combine flag and argument`() {
        class MixedAction : Action() {
            val flag by flag("flag")
            val arg by argument("arg")
        }

        parse(MixedAction(), listOf("--flag", "abc")) { action ->
            assertTrue(action.flag)
            assertEquals("abc", action.arg)
        }

        parse(MixedAction(), listOf("abc", "--flag")) { action ->
            assertTrue(action.flag)
            assertEquals("abc", action.arg)
        }

        parse(MixedAction(), listOf("abc")) { action ->
            assertFalse(action.flag)
            assertEquals("abc", action.arg)
        }
    }
}