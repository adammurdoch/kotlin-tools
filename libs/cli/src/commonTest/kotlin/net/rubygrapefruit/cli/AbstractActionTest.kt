package net.rubygrapefruit.cli

import kotlin.test.fail
import kotlin.test.assertEquals

abstract class AbstractActionTest {
    fun <T : Action> parse(action: T, args: List<String>, verification: (T) -> Unit) {
        action.parse(args)
        verification(action)
    }

    fun parseFails(action: Action, args: List<String>, message: String) {
        try {
            action.parse(args)
            fail()
        } catch (e: ArgParseException) {
            assertEquals(message, e.message)
        }
    }
}