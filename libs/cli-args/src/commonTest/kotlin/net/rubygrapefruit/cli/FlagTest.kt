package net.rubygrapefruit.cli

import kotlin.test.*

class FlagTest : AbstractActionTest() {
    @Test
    fun `can enable boolean option with long name`() {
        class Flag : Action() {
            val flag by flag("flag")
        }

        parse(Flag(), listOf("--flag")) { action ->
            assertTrue(action.flag)
        }

        parseFails(::Flag, listOf("-flag"), "Unknown option: -flag")
    }

    @Test
    fun `can disable boolean option with long name`() {
        class Flag : Action() {
            val flag by flag("flag")
        }

        parse(Flag(), listOf("--no-flag")) { action ->
            assertFalse(action.flag)
        }

        parseFails(::Flag, listOf("-no-flag"), "Unknown option: -no-flag")
    }

    @Test
    fun `can enable boolean option with short name`() {
        class Flag : Action() {
            val flag by flag("f")
        }

        parse(Flag(), listOf("-f")) { action ->
            assertTrue(action.flag)
        }

        parseFails(::Flag, listOf("--f"), "Unknown option: --f")
    }

    @Test
    fun `cannot disable boolean option with short name`() {
        class Flag : Action() {
            val flag by flag("f")
        }

        parseFails(::Flag, listOf("--no-f"), "Unknown option: --no-f")
        parseFails(::Flag, listOf("-no-f"), "Unknown option: -no-f")
    }

    @Test
    fun `can opt to have no disable option`() {
        class Flag : Action() {
            val flag by boolean().flag("flag", disableOption = false)
        }

        parseFails(::Flag, listOf("--no-flag"), "Unknown option: --no-flag")
        parseFails(::Flag, listOf("-no-flag"), "Unknown option: -no-flag")
    }

    @Test
    fun `can define multiple names for flag`() {
        class Flag : Action() {
            val flag by flag("f", "flag")
        }

        parse(Flag(), listOf("--flag")) { action ->
            assertTrue(action.flag)
        }
        parse(Flag(), listOf("-f")) { action ->
            assertTrue(action.flag)
        }

        parse(Flag(), listOf("--no-flag")) { action ->
            assertFalse(action.flag)
        }
    }

    @Test
    fun `boolean option with long name can be present multiple times`() {
        class Flag : Action() {
            val flag by flag("flag")
        }

        parse(Flag(), listOf("--flag", "--flag")) { action ->
            assertTrue(action.flag)
        }
    }

    @Test
    fun `boolean option with show name can be present multiple times`() {
        class Flag : Action() {
            val flag by flag("f")
        }

        parse(Flag(), listOf("-f", "-f")) { action ->
            assertTrue(action.flag)
        }
    }

    @Test
    fun `can override boolean option with long name`() {
        class Flag : Action() {
            val flag by flag("flag")
        }

        parse(Flag(), listOf("--flag", "--no-flag")) { action ->
            assertFalse(action.flag)
        }

        parse(Flag(), listOf("--no-flag", "--flag")) { action ->
            assertTrue(action.flag)
        }
    }

    @Test
    fun `flag defaults to false`() {
        class Flag : Action() {
            val f by flag("f")
            val flag by flag("flag")
        }

        parse(Flag(), emptyList()) { action ->
            assertFalse(action.f)
            assertFalse(action.flag)
        }
    }

    @Test
    fun `can enable multiple boolean options in any order`() {
        class Flag : Action() {
            val f1 by flag("f1")
            val f2 by flag("f2")
        }

        parse(Flag(), listOf("--f1", "--f2")) { action ->
            assertTrue(action.f1)
            assertTrue(action.f2)
        }

        parse(Flag(), listOf("--f2", "--f1")) { action ->
            assertTrue(action.f1)
            assertTrue(action.f2)
        }
    }

    @Test
    fun `name must not start with punctuation`() {
        try {
            Action().flag("-f")
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-f cannot be used as a flag name", e.message)
        }
        try {
            Action().flag("f", "--flag")
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--flag cannot be used as a flag name", e.message)
        }
    }

    @Test
    fun `names must be unique`() {
        val action = Action()
        action.option("o", "option")
        try {
            action.flag("o")
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-o is used by another parameter", e.message)
        }
    }
}