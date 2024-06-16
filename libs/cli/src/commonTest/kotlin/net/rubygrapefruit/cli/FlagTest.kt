package net.rubygrapefruit.cli

import kotlin.test.*

class FlagTest : AbstractActionTest() {
    @Test
    fun `can enable boolean option with long name`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        parse(BooleanFlag(), listOf("--flag")) { action ->
            assertTrue(action.flag)
        }

        parseFails(BooleanFlag(), listOf("-flag"), "Unknown option: -flag")
    }

    @Test
    fun `can disable boolean option with long name`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        parse(BooleanFlag(), listOf("--no-flag")) { action ->
            assertFalse(action.flag)
        }

        parseFails(BooleanFlag(), listOf("-no-flag"), "Unknown option: -no-flag")
    }

    @Test
    fun `can enable boolean option with short name`() {
        class BooleanFlag : Action() {
            val flag by flag("f")
        }

        parse(BooleanFlag(), listOf("-f")) { action ->
            assertTrue(action.flag)
        }

        parseFails(BooleanFlag(), listOf("--f"), "Unknown option: --f")
    }

    @Test
    fun `cannot disable boolean option with short name`() {
        class BooleanFlag : Action() {
            val flag by flag("f")
        }

        parseFails(BooleanFlag(), listOf("--no-f"), "Unknown option: --no-f")
        parseFails(BooleanFlag(), listOf("-no-f"), "Unknown option: -no-f")
    }

    @Test
    fun `can define multiple names for flag`() {
        class BooleanFlag : Action() {
            val flag by flag("f", "flag")
        }

        parse(BooleanFlag(), listOf("--flag")) { action ->
            assertTrue(action.flag)
        }
        parse(BooleanFlag(), listOf("-f")) { action ->
            assertTrue(action.flag)
        }

        parse(BooleanFlag(), listOf("--no-flag")) { action ->
            assertFalse(action.flag)
        }
    }

    @Test
    fun `boolean option with long name can be present multiple times`() {
        class BooleanFlag : Action() {
            val flag by flag("flag")
        }

        parse(BooleanFlag(), listOf("--flag", "--flag")) { action ->
            assertTrue(action.flag)
        }
    }

    @Test
    fun `boolean option with show name can be present multiple times`() {
        class BooleanFlag : Action() {
            val flag by flag("f")
        }

        parse(BooleanFlag(), listOf("-f", "-f")) { action ->
            assertTrue(action.flag)
        }
    }

    @Test
    fun `can override boolean option with long name`() {
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
            val f by flag("f")
            val flag by flag("flag")
        }

        parse(BooleanFlag(), emptyList()) { action ->
            assertFalse(action.f)
            assertFalse(action.flag)
        }
    }

    @Test
    fun `can provide default value for boolean option`() {
        class BooleanFlag : Action() {
            val f1 by flag("f1").whenAbsent(true)
            val f2 by flag("f2").whenAbsent(false)
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
    fun `can enable multiple boolean options in any order`() {
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
        class Broken1 : Action() {
            val flag by flag("-f")
        }
        try {
            Broken1()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-f cannot be used as a flag name", e.message)
        }
        class Broken2 : Action() {
            val flag by flag("f", "--flag")
        }
        try {
            Broken2()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--flag cannot be used as a flag name", e.message)
        }
    }
}