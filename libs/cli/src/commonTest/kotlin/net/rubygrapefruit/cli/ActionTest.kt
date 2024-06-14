package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActionTest {
    @Test
    fun `action can have no configuration`() {
        class NoConfig : Action()

        NoConfig().parse(emptyList())
    }

    @Test
    fun `can enable boolean option`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        val action = BooleanFlag()
        action.parse(listOf("--flag"))

        assertTrue(action.flag)
    }

    @Test
    fun `can disable boolean option`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        val action = BooleanFlag()
        action.parse(listOf("--no-flag"))

        assertFalse(action.flag)
    }

    @Test
    fun `can override boolean option`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        val action = BooleanFlag()
        action.parse(listOf("--flag", "--no-flag"))

        assertFalse(action.flag)

        val action2 = BooleanFlag()
        action2.parse(listOf("--no-flag", "--flag"))

        assertTrue(action2.flag)
    }
}