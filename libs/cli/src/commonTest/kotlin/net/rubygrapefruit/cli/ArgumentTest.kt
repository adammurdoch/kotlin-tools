package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ArgumentTest : AbstractActionTest() {
    @Test
    fun `action can have argument`() {
        class Argument : Action() {
            val arg by argument("value")
        }

        parse(Argument(), listOf("abc")) { action ->
            assertEquals("abc", action.arg)
        }
    }

    @Test
    fun `action can have multiple arguments`() {
        class Argument : Action() {
            val a1 by argument("value")
            val a2 by argument("value")
        }

        parse(Argument(), listOf("abc", "def")) { action ->
            assertEquals("abc", action.a1)
            assertEquals("def", action.a2)
        }
    }

    @Test
    fun `can provide default value for argument`() {
        class Argument : Action() {
            val arg by argument("value", default = "value")
        }

        parse(Argument(), emptyList()) { action ->
            assertEquals("value", action.arg)
        }

        parse(Argument(), listOf("123")) { action ->
            assertEquals("123", action.arg)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class Argument : Action() {
            val arg by argument("value")
        }

        parseFails(Argument(), emptyList(), "Argument 'value' not provided")
    }

    @Test
    fun `fails when some arguments provided but not all`() {
        class Argument : Action() {
            val a1 by argument("a1")
            val a2 by argument("a2")
        }

        parseFails(Argument(), listOf("abc"), "Argument 'a2' not provided")
    }

    @Test
    fun `fails when unknown flag provided`() {
        class Argument : Action() {
            val arg by argument("value")
        }

        parseFails(Argument(), listOf("--flag"), "Unknown option: --flag")
    }

    @Test
    fun `fails when additional argument provided`() {
        class Argument : Action() {
            val arg by argument("value")
        }

        parseFails(Argument(), listOf("1", "2"), "Unknown argument: 2")
    }

    @Test
    fun `can run --help command without providing argument`() {
        class Argument : MainAction("cmd") {
            val arg by argument("value")
        }

        val action = Argument().actionFor(listOf("--help"))
        assertIs<HelpAction>(action)
    }
}