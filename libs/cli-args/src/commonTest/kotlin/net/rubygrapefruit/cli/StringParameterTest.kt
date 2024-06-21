package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StringParameterTest : AbstractActionTest() {
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
    fun `fails when parameter not provided`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parseFails(Parameter(), emptyList(), "Parameter 'value' not provided")
    }

    @Test
    fun `can provide default value for parameter`() {
        class Parameter : Action() {
            val param by parameter("value").whenAbsent("value")
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals("value", action.param)
        }

        parse(Parameter(), listOf("123")) { action ->
            assertEquals("123", action.param)
        }
    }

    @Test
    fun `action can have optional parameter`() {
        class Parameter : Action() {
            val param by parameter("value").optional()
        }

        parse(Parameter(), emptyList()) { action ->
            assertNull(action.param)
        }

        parse(Parameter(), listOf("123")) { action ->
            assertEquals("123", action.param)
        }
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
    fun `can provide default value for parameters`() {
        class Parameter : Action() {
            val p1 by parameter("p1").whenAbsent("p1")
            val p2 by parameter("p2").whenAbsent("p2")
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals("p1", action.p1)
            assertEquals("p2", action.p2)
        }

        parse(Parameter(), listOf("123")) { action ->
            assertEquals("123", action.p1)
            assertEquals("p2", action.p2)
        }

        parse(Parameter(), listOf("123", "abc")) { action ->
            assertEquals("123", action.p1)
            assertEquals("abc", action.p2)
        }
    }

    @Test
    fun `fails when unknown flag provided instead of parameter`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parseFails(Parameter(), listOf("--flag"), "Unknown option: --flag")
        parseFails(Parameter(), listOf("--flag", "arg"), "Unknown option: --flag")
        parseFails(Parameter(), listOf("arg", "--flag"), "Unknown option: --flag")
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
        class Parameter : TestApp("cmd") {
            val param by parameter("value")
        }

        parseRecovers(Parameter(), listOf("--help")) { action ->
            assertTrue(action.help)
        }
    }
}