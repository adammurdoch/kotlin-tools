package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class ChoiceFlagsTest : AbstractActionTest() {
    @Test
    fun `can choose one of several values using options with long names`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "one")
                choice(2, "two")
                choice(3, "three")
            }.flags()
        }

        parse(Choice(), listOf("--one")) { action ->
            assertEquals(1, action.selected)
        }
        parse(Choice(), listOf("--two")) { action ->
            assertEquals(2, action.selected)
        }
        parse(Choice(), listOf("--three")) { action ->
            assertEquals(3, action.selected)
        }

        parseFails(::Choice, listOf("-one"), "Unknown option: -one")
    }

    @Test
    fun `can choose one of several values using options with short names`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1")
                choice(2, "2")
                choice(3, "3")
            }.flags()
        }

        parse(Choice(), listOf("-1")) { action ->
            assertEquals(1, action.selected)
        }
        parse(Choice(), listOf("-2")) { action ->
            assertEquals(2, action.selected)
        }
        parse(Choice(), listOf("-3")) { action ->
            assertEquals(3, action.selected)
        }

        parseFails(::Choice, listOf("--1"), "Unknown option: --1")
    }

    @Test
    fun `can define multiple names for choice item`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1", "one")
                choice(2, "two")
                choice(3, "three")
            }.flags()
        }

        parse(Choice(), listOf("-1")) { action ->
            assertEquals(1, action.selected)
        }
        parse(Choice(), listOf("--one")) { action ->
            assertEquals(1, action.selected)
        }

        parseFails(::Choice, listOf("-one"), "Unknown option: -one")
        parseFails(::Choice, listOf("--1"), "Unknown option: --1")
    }

    @Test
    fun `can provide default value for choice`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1")
                choice(2, "2")
                choice(3, "3")
            }.flags().whenAbsent(2)
        }

        parse(Choice(), emptyList()) { action ->
            assertEquals(2, action.selected)
        }

        parse(Choice(), listOf("-3")) { action ->
            assertEquals(3, action.selected)
        }
    }

    @Test
    fun `value is null when flags are not present`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1")
                choice(2, "2")
                choice(3, "3")
            }.flags()
        }

        parse(Choice(), emptyList()) { action ->
            assertNull(action.selected)
        }
    }

    @Test
    fun `flag can appear multiple times`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1", "one")
                choice(2, "2")
                choice(3, "3")
            }.flags()
        }
        parse(Choice(), listOf("-1", "-1", "--one")) { action ->
            assertEquals(1, action.selected)
        }
    }

    @Test
    fun `can override value with multiple flags`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1")
                choice(2, "2", "two")
                choice(3, "3")
            }.flags()
        }

        parse(Choice(), listOf("-1", "-2")) { action ->
            assertEquals(2, action.selected)
        }
        parse(Choice(), listOf("-1", "--two")) { action ->
            assertEquals(2, action.selected)
        }
        parse(Choice(), listOf("-3", "-2", "-1")) { action ->
            assertEquals(1, action.selected)
        }
    }

    @Test
    fun `can require flags to be present`() {
        class Option : Action() {
            val option by oneOf {
                choice(1, "1", "one")
                choice(2, "two")
            }.flags().required()
        }

        parse(Option(), listOf("--two")) { action ->
            assertEquals(2, action.option)
        }
        parse(Option(), listOf("--two", "-1")) { action ->
            assertEquals(1, action.option)
        }
        parseFails(::Option, emptyList(), "One of the following options must be provided: --one, --two")
    }

    @Test
    fun `name must not start with punctuation`() {
        try {
            Action().oneOf {
                choice(1, "--one")
            }.flags()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--one cannot be used as a choice name", e.message)
        }
        try {
            Action().oneOf {
                choice(1, "one", "-1")
            }.flags()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-1 cannot be used as a choice name", e.message)
        }
    }

    @Test
    fun `names must be unique`() {
        val action = Action()
        action.option("o", "option")
        try {
            action.oneOf {
                choice(1, "o", "1")
            }.flags()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-o is used by another parameter", e.message)
        }
    }
}