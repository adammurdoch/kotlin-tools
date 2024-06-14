package net.rubygrapefruit.cli

import kotlin.test.*

class ActionFlagTest {
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

    @Test
    fun `flag defaults to false`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        val action = BooleanFlag()
        action.parse(emptyList())

        assertFalse(action.flag)
    }

    @Test
    fun `can provide default value for boolean option`() {
        class BooleanFlag : Action() {
            val f1 by flag("flag", default = true)
            val f2 by flag("flag", default = false)
        }

        val action = BooleanFlag()
        action.parse(emptyList())

        assertTrue(action.f1)
        assertFalse(action.f2)
    }

    @Test
    fun `can enable multiple boolean options`() {
        class BooleanFlag : Action() {
            val f1 by flag("f1")
            val f2 by flag("f2")
        }

        val action = BooleanFlag()
        action.parse(listOf("--f1", "--f2"))

        assertTrue(action.f1)
        assertTrue(action.f2)
    }
}