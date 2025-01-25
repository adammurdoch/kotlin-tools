package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceFlagsListTest : AbstractActionTest() {
    @Test
    fun `action can have option with list value`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1")
                choice(2, "two")
                choice(3, "3")
            }.flags().repeated()
        }

        parse(::Choice, emptyList()) { action ->
            assertEquals(emptyList(), action.selected)
        }
        parse(::Choice, listOf("-1")) { action ->
            assertEquals(listOf(1), action.selected)
        }
        parse(::Choice, listOf("-1", "--two", "-3")) { action ->
            assertEquals(listOf(1, 2, 3), action.selected)
        }
        parse(::Choice, listOf("-1", "-1")) { action ->
            assertEquals(listOf(1, 1), action.selected)
        }
    }

    @Test
    fun `fails when unknown option used`() {
        class Choice : Action() {
            val selected by oneOf {
                choice(1, "1")
                choice(2, "two")
                choice(3, "3")
            }.flags().repeated()
        }

        parseFails(::Choice, listOf("--unknown"), "Unknown option: --unknown")
        parseFails(::Choice, listOf("-1", "--unknown"), "Unknown option: --unknown")
        parseFails(::Choice, listOf("-1", "--unknown", "-3"), "Unknown option: --unknown")
    }
}