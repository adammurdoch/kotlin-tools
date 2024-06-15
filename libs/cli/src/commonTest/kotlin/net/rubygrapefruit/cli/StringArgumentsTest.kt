package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class StringArgumentsTest : AbstractActionTest() {
    @Test
    fun `action can have zero or more arguments`() {
        class Argument : Action() {
            val arg by parameters("value")
        }

        parse(Argument(), emptyList()) { action ->
            assertEquals(emptyList(), action.arg)
        }
        parse(Argument(), listOf("abc")) { action ->
            assertEquals(listOf("abc"), action.arg)
        }
        parse(Argument(), listOf("a", "b", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.arg)
        }
    }
}