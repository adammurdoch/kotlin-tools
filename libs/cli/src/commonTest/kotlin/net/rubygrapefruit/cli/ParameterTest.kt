package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ParameterTest : AbstractActionTest() {
    @Test
    fun `action can have parameter`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parse(Parameter(), listOf("abc")) { action ->
            assertEquals("abc", action.param)
        }
    }

    @Test
    fun `action can have multiple parameters`() {
        class Parameter : Action() {
            val p1 by parameter("value")
            val p2 by parameter("value")
        }

        parse(Parameter(), listOf("abc", "def")) { action ->
            assertEquals("abc", action.p1)
            assertEquals("def", action.p2)
        }
    }

    @Test
    fun `can provide default value for parameter`() {
        class Parameter : Action() {
            val param by parameter("value", default = "value")
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals("value", action.param)
        }

        parse(Parameter(), listOf("123")) { action ->
            assertEquals("123", action.param)
        }
    }

    @Test
    fun `fails when parameter not provided`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parseFails(Parameter(), emptyList(), "Parameter 'value' not provided")
    }

    @Test
    fun `fails when some parameters provided but not all`() {
        class Parameter : Action() {
            val p1 by parameter("a1")
            val p2 by parameter("a2")
        }

        parseFails(Parameter(), listOf("abc"), "Parameter 'a2' not provided")
    }

    @Test
    fun `fails when unknown flag provided`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parseFails(Parameter(), listOf("--flag"), "Unknown option: --flag")
    }

    @Test
    fun `fails when additional parameter provided`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parseFails(Parameter(), listOf("1", "2"), "Unknown parameter: 2")
    }

    @Test
    fun `can run --help command without providing parameter`() {
        class Parameter : MainAction("cmd") {
            val param by parameter("value")
        }

        val action = Parameter().actionFor(listOf("--help"))
        assertIs<HelpAction>(action)
    }
}