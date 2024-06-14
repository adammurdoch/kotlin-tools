package net.rubygrapefruit.cli

import kotlin.test.*

class FlagTest : AbstractActionTest() {
    @Test
    fun `can enable boolean option`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        parse(BooleanFlag(), listOf("--flag")) { action ->
            assertTrue(action.flag)
        }
    }

    @Test
    fun `can disable boolean option`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        parse(BooleanFlag(), listOf("--no-flag")) { action ->
            assertFalse(action.flag)
        }
    }

    @Test
    fun `can override boolean option`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        parse(BooleanFlag(), listOf("--flag", "--no-flag")) { action ->
            assertFalse(action.flag)
        }

        parse(BooleanFlag(), listOf("--no-flag", "--flag")) { action ->
            assertTrue(action.flag)
        }
    }

    @Test
    fun `flag defaults to false`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        parse(BooleanFlag(), emptyList()) { action ->
            assertFalse(action.flag)
        }
    }

    @Test
    fun `can provide default value for boolean option`() {
        class BooleanFlag : Action() {
            val f1 by flag("f1", default = true)
            val f2 by flag("f2", default = false)
        }

        parse(BooleanFlag(), emptyList()) { action ->
            assertTrue(action.f1)
            assertFalse(action.f2)
        }

        parse(BooleanFlag(), listOf("--no-f1", "--f2")) { action ->
            assertFalse(action.f1)
            assertTrue(action.f2)
        }
    }

    @Test
    fun `can enable multiple boolean options`() {
        class BooleanFlag : Action() {
            val f1 by flag("f1")
            val f2 by flag("f2")
        }

        parse(BooleanFlag(), listOf("--f1", "--f2")) { action ->
            assertTrue(action.f1)
            assertTrue(action.f2)
        }

        parse(BooleanFlag(), listOf("--f2", "--f1")) { action ->
            assertTrue(action.f1)
            assertTrue(action.f2)
        }
    }

    @Test
    fun `name must not start with punctuation`() {
        class BooleanFlag : Action() {
            val flag by flag("-f")
        }
        try {
            BooleanFlag()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-f cannot be used as a flag name", e.message)
        }
    }
}