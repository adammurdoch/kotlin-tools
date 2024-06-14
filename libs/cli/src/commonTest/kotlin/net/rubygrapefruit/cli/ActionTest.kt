package net.rubygrapefruit.cli

import kotlin.test.*

class ActionTest {
    @Test
    fun `action can have no configuration`() {
        class NoConfig : Action()

        NoConfig().parse(emptyList())
    }

    @Test
    fun `fails when args provided for action with no configuration`() {
        class NoConfig : Action()

        try {
            NoConfig().parse(listOf("--flag"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Unknown option: --flag", e.message)
        }
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
    fun `can provide default value for boolean option not provided`() {
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