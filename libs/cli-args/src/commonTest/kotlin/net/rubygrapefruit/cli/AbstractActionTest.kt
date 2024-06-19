package net.rubygrapefruit.cli

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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

    fun <T : Action> parseRecovers(action: T, args: List<String>, verification: (T) -> Unit) {
        val result = action.parseAll(args)
        assertNotNull(result.failure)
        verification(action)
    }
}