package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActionArgumentAndFlagTest {
    @Test
    fun `flag can appear before argument`() {
        class MixedAction : Action() {
            val flag by flag("flag")
            val arg by argument("arg")
        }

        val action = MixedAction()
        action.parse(listOf("--flag", "abc"))

        assertTrue(action.flag)
        assertEquals("abc", action.arg)
    }

    @Test
    fun `flag can appear after argument`() {
        class MixedAction : Action() {
            val flag by flag("flag")
            val arg by argument("arg")
        }

        val action = MixedAction()
        action.parse(listOf("abc", "--flag"))

        assertTrue(action.flag)
        assertEquals("abc", action.arg)
    }
}