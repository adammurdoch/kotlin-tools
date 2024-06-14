package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ArgumentTest {
    @Test
    fun `action can have argument`() {
        class Argument : Action() {
            val arg by argument("value")
        }

        val action = Argument()
        action.parse(listOf("abc"))

        assertEquals("abc", action.arg)
    }

    @Test
    fun `action can have multiple arguments`() {
        class Argument : Action() {
            val a1 by argument("value")
            val a2 by argument("value")
        }

        val action = Argument()
        action.parse(listOf("abc", "def"))

        assertEquals("abc", action.a1)
        assertEquals("def", action.a2)
    }

    @Test
    fun `can provide default value for argument`() {
        class Argument : Action() {
            val arg by argument("value", default = "thing")
        }

        val action = Argument()
        action.parse(emptyList())

        assertEquals("thing", action.arg)
    }

    @Test
    fun `fails when argument not provided`() {
        class Argument : Action() {
            val arg by argument("value")
        }

        val action = Argument()
        try {
            action.parse(emptyList())
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Argument 'value' not provided", e.message)
        }
    }

    @Test
    fun `fails when some arguments provided but not all`() {
        class Argument : Action() {
            val a1 by argument("a1")
            val a2 by argument("a2")
        }

        val action = Argument()
        try {
            action.parse(listOf("abc"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Argument 'a2' not provided", e.message)
        }
    }

    @Test
    fun `fails when additional argument provided`() {
        class Argument : Action() {
            val arg by argument("value")
        }

        val action = Argument()
        try {
            action.parse(listOf("1", "2"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Unknown argument: 2", e.message)
        }
    }
}