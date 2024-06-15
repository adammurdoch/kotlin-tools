package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ArgumentTest : AbstractActionTest() {
    @Test
    fun `action can have argument`() {
        class Argument : Action() {
            val arg by parameter("value")
        }

        parse(Argument(), listOf("abc")) { action ->
            assertEquals("abc", action.arg)
        }
    }

    @Test
    fun `action can have multiple arguments`() {
        class Argument : Action() {
            val a1 by parameter("value")
            val a2 by parameter("value")
        }

        parse(Argument(), listOf("abc", "def")) { action ->
            assertEquals("abc", action.a1)
            assertEquals("def", action.a2)
        }
    }

    @Test
    fun `can provide default value for argument`() {
        class Argument : Action() {
            val arg by parameter("value", default = "value")
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
            val arg by parameter("value")
        }

        parseFails(Argument(), emptyList(), "Parameter 'value' not provided")
    }

    @Test
    fun `fails when some arguments provided but not all`() {
        class Argument : Action() {
            val a1 by parameter("a1")
            val a2 by parameter("a2")
        }

        parseFails(Argument(), listOf("abc"), "Parameter 'a2' not provided")
    }

    @Test
    fun `fails when unknown flag provided`() {
        class Argument : Action() {
            val arg by parameter("value")
        }

        parseFails(Argument(), listOf("--flag"), "Unknown option: --flag")
    }

    @Test
    fun `fails when additional argument provided`() {
        class Argument : Action() {
            val arg by parameter("value")
        }

        parseFails(Argument(), listOf("1", "2"), "Unknown parameter: 2")
    }

    @Test
    fun `can run --help command without providing argument`() {
        class Argument : MainAction("cmd") {
            val arg by parameter("value")
        }

        val action = Argument().actionFor(listOf("--help"))
        assertIs<HelpAction>(action)
    }
}