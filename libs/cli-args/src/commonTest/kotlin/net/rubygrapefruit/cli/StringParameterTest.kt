package net.rubygrapefruit.cli

import kotlin.test.*

class StringParameterTest : AbstractActionTest() {
    @Test
    fun `action can have parameter`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parse(::Parameter, listOf("abc")) { action ->
            assertEquals("abc", action.param)
        }
    }

    @Test
    fun `action can have multiple parameters`() {
        class Parameter : Action() {
            val p1 by parameter("value")
            val p2 by parameter("value")
        }

        parse(::Parameter, listOf("abc", "def")) { action ->
            assertEquals("abc", action.p1)
            assertEquals("def", action.p2)
        }
    }

    @Test
    fun `fails when parameter not provided`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parseFails(::Parameter, emptyList(), "Parameter 'value' not provided", "<param>")
    }

    @Test
    fun `fails when some parameters provided but not all`() {
        class Parameter : Action() {
            val p1 by parameter("a1")
            val p2 by parameter("a2")
        }

        parseFails(::Parameter, listOf("abc"), "Parameter 'a2' not provided", "<param>", "<param>")
    }

    @Test
    fun `can provide default value for parameter`() {
        class Parameter : Action() {
            val param by parameter("value").whenAbsent("value")
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals("value", action.param)
        }

        parse(::Parameter, listOf("123")) { action ->
            assertEquals("123", action.param)
        }
    }

    @Test
    fun `can provide default value for parameters`() {
        class Parameter : Action() {
            val p1 by parameter("p1").whenAbsent("p1")
            val p2 by parameter("p2").whenAbsent("p2")
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals("p1", action.p1)
            assertEquals("p2", action.p2)
        }

        parse(::Parameter, listOf("123")) { action ->
            assertEquals("123", action.p1)
            assertEquals("p2", action.p2)
        }

        parse(::Parameter, listOf("123", "abc")) { action ->
            assertEquals("123", action.p1)
            assertEquals("abc", action.p2)
        }
    }

    @Test
    fun `action can have optional parameter`() {
        class Parameter : Action() {
            val param by parameter("value").optional()
        }

        parse(::Parameter, emptyList()) { action ->
            assertNull(action.param)
        }

        parse(::Parameter, listOf("123")) { action ->
            assertEquals("123", action.param)
        }
    }

    @Test
    fun `action can have optional parameters`() {
        class Parameter : Action() {
            val p1 by parameter("value1").optional()
            val p2 by parameter("value2").optional()
        }

        parse(::Parameter, emptyList()) { action ->
            assertNull(action.p1)
            assertNull(action.p2)
        }

        parse(::Parameter, listOf("123")) { action ->
            assertEquals("123", action.p1)
            assertNull(action.p2)
        }

        parse(::Parameter, listOf("123", "abc")) { action ->
            assertEquals("123", action.p1)
            assertEquals("abc", action.p2)
        }
    }

    @Test
    fun `fails when flag provided instead of parameter`() {
        class Parameter : Action() {
            val param by parameter("value")
            val flag by flag("f", "flag")
        }

        parseFails(::Parameter, listOf("--flag"), "Parameter 'value' not provided", "<param>")
        parseFails(::Parameter, listOf("-f"), "Parameter 'value' not provided", "<param>")
        // Prefer reporting an unknown option
        parseFails(::Parameter, listOf("--unknown"), "Unknown option: --unknown")
        parseFails(::Parameter, listOf("-u"), "Unknown option: -u")
    }

    @Test
    fun `reports unknown flag used with parameter`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parseFails(::Parameter, listOf("arg", "--flag"), "Unknown option: --flag")
        parseFails(::Parameter, listOf("--flag", "arg"), "Unknown option: --flag")
    }

    @Test
    fun `reports unknown flag used with parameter with default value`() {
        class Parameter : Action() {
            val param by parameter("value").whenAbsent("value")
        }

        parseFails(::Parameter, listOf("arg", "--flag"), "Unknown option: --flag")
        parseFails(::Parameter, listOf("--flag", "arg"), "Unknown option: --flag")
        parseFails(::Parameter, listOf("--flag"), "Unknown option: --flag")
    }

    @Test
    fun `reports unknown flag used with optional parameter`() {
        class Parameter : Action() {
            val param by parameter("value").optional()
        }

        parseFails(::Parameter, listOf("arg", "--flag"), "Unknown option: --flag")
        parseFails(::Parameter, listOf("--flag", "arg"), "Unknown option: --flag")
        parseFails(::Parameter, listOf("--flag"), "Unknown option: --flag")
    }

    @Test
    fun `fails when additional parameter provided`() {
        class Parameter : Action() {
            val param by parameter("value")
        }

        parseFails(::Parameter, listOf("1", "2"), "Unknown parameter: 2", "<param>")
    }

    @Test
    fun `fails when additional parameter provided for parameter with default value`() {
        class Parameter : Action() {
            val param by parameter("value").whenAbsent("value")
        }

        parseFails(::Parameter, listOf("1", "2"), "Unknown parameter: 2", "<param>")
    }

    @Test
    fun `fails when additional parameter provided for optional parameter`() {
        class Parameter : Action() {
            val param by parameter("value").optional()
        }

        parseFails(::Parameter, listOf("1", "2"), "Unknown parameter: 2", "<param>")
    }

    @Test
    fun `parameter name must not start with punctuation`() {
        class Broken1 : Action() {
            val param by parameter("-p")
        }
        try {
            Broken1()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-p cannot be used as a parameter name", e.message)
        }

        class Broken2 : Action() {
            val param by parameter("--param")
        }
        try {
            Broken2()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--param cannot be used as a parameter name", e.message)
        }
    }
}