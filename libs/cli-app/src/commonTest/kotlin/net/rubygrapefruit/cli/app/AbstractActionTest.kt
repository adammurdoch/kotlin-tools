package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ArgParseException
import kotlin.test.assertEquals
import kotlin.test.fail

abstract class AbstractActionTest {
    fun <T : Action> parse(action: T, args: List<String>, verification: (T) -> Unit) {
        action.parse(args)
        verification(action)
    }

    fun parseFails(action: Action, args: List<String>, message: String) {
        parseFails(action, args) { e -> assertEquals(message, e.message) }
    }

    fun parseFails(action: Action, args: List<String>, verification: (ArgParseException) -> Unit) {
        try {
            action.parse(args)
            fail()
        } catch (e: ArgParseException) {
            verification(e)
        }
    }
}