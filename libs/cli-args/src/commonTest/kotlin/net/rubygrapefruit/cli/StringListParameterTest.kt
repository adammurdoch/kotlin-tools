package net.rubygrapefruit.cli

import kotlin.test.*

class StringListParameterTest : AbstractActionTest() {
    @Test
    fun `action can have parameter with list value`() {
        class Parameter : Action() {
            val param by parameter("value").repeated()
        }

        parseFails(::Parameter, emptyList(), "Parameter 'value' not provided", "<param>")

        parse(::Parameter, listOf("abc")) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(::Parameter, listOf("a", "b", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.param)
        }
    }

    @Test
    fun `can define a default value`() {
        class Parameter : Action() {
            val param by string().parameter("value").repeated().whenAbsent(listOf("abc"))
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(::Parameter, listOf("abc")) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(::Parameter, listOf("a", "b", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.param)
        }
    }

    @Test
    fun `can make parameter optional`() {
        class Parameter : Action() {
            val param by string().parameter("value").repeated().optional()
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(::Parameter, listOf("abc")) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(::Parameter, listOf("a", "b", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.param)
        }
    }

    @Test
    fun `fails when unknown flag provided with arguments`() {
        class Parameter : Action() {
            val param by parameter("value").repeated()
        }

        parseFails(::Parameter, listOf("--flag"), "Unknown option: --flag")
        parseFails(::Parameter, listOf("arg", "--flag"), "Unknown option: --flag")
        parseFails(::Parameter, listOf("--flag", "arg"), "Unknown option: --flag")
        parseFails(::Parameter, listOf("arg", "--flag", "arg"), "Unknown option: --flag")
    }

    @Test
    fun `fails when flag provided instead of argument`() {
        class Parameter : Action() {
            val param by string().parameter("value").repeated()
            val flag by flag("f", "flag")
        }

        parseFails(::Parameter, listOf("-f"), "Parameter 'value' not provided", "<param>")
        parseFails(::Parameter, listOf("--flag"), "Parameter 'value' not provided", "<param>")
        parseFails(::Parameter, listOf("-u"), "Unknown option: -u")
        parseFails(::Parameter, listOf("--unknown"), "Unknown option: --unknown")
    }

    @Test
    fun `can consume remaining arguments`() {
        class Parameter : Action() {
            val param by remainder("value")
            val flag by flag("flag")
        }

        parse(::Parameter, listOf("--flag")) { action ->
            assertEquals(emptyList(), action.param)
            assertTrue(action.flag)
        }
        parse(::Parameter, listOf("--flag", "a")) { action ->
            assertEquals(listOf("a"), action.param)
            assertTrue(action.flag)
        }
        parse(::Parameter, listOf("--other")) { action ->
            assertEquals(listOf("--other"), action.param)
            assertFalse(action.flag)
        }
        parse(::Parameter, listOf("a", "--flag", "b", "--other")) { action ->
            assertEquals(listOf("a", "--flag", "b", "--other"), action.param)
            assertFalse(action.flag)
        }
        parse(::Parameter, listOf("--flag", "a", "--flag", "b", "--other")) { action ->
            assertEquals(listOf("a", "--flag", "b", "--other"), action.param)
            assertTrue(action.flag)
        }
    }

    @Test
    fun `can consume remaining arguments including --help`() {
        class Parameter : Action() {
            val param by remainder("value")
        }

        parse(TestApp(Parameter()), listOf("--help")) { action ->
            assertIs<HelpAction>(action.selected)
        }
        parse(TestApp(Parameter()), listOf("a", "--help")) { action ->
            val selected = action.selected
            assertIs<Parameter>(selected)
            assertEquals(listOf("a", "--help"), selected.param)
        }
    }

    @Test
    fun `can require at least one remaining argument to be present`() {
        class Parameter : Action() {
            val param by remainder("value").required()
        }

        parseFails(::Parameter, emptyList(), "Parameter 'value' not provided", "<param>")

        parse(::Parameter, listOf("--flag")) { action ->
            assertEquals(listOf("--flag"), action.param)
        }
        parse(::Parameter, listOf("--flag", "a")) { action ->
            assertEquals(listOf("--flag", "a"), action.param)
        }
    }

    @Test
    fun `parameter name must not start with punctuation`() {
        class Broken1 : Action() {
            val param by parameter("-p").repeated()
        }
        try {
            Broken1()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-p cannot be used as a parameter name", e.message)
        }
        class Broken2 : Action() {
            val param by parameter("--param").repeated()
        }
        try {
            Broken2()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--param cannot be used as a parameter name", e.message)
        }
        class Broken3 : Action() {
            val param by remainder("-p")
        }
        try {
            Broken3()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-p cannot be used as a parameter name", e.message)
        }
    }
}