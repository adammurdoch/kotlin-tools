package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChoiceTest : AbstractActionTest() {
    @Test
    fun `can choose one of several values using options with long names`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "one")
                choice(2, "two")
                choice(3, "three")
            }
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

        parseFails(Choice(), listOf("-one"), "Unknown option: -one")
    }

    @Test
    fun `can choose one of several values using options with short names`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1")
                choice(2, "2")
                choice(3, "3")
            }
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

        parseFails(Choice(), listOf("--1"), "Unknown option: --1")
    }

    @Test
    fun `can give choice multiple names`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1", "one")
                choice(2, "two")
                choice(3, "three")
            }
        }

        parse(Choice(), listOf("-1")) { action ->
            assertEquals(1, action.selected)
        }
        parse(Choice(), listOf("--one")) { action ->
            assertEquals(1, action.selected)
        }

        parseFails(Choice(), listOf("-one"), "Unknown option: -one")
        parseFails(Choice(), listOf("--1"), "Unknown option: --1")
    }

    @Test
    fun `value is null when flags are not present`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1")
                choice(2, "2")
                choice(3, "3")
            }
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
            }
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
            }
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
}