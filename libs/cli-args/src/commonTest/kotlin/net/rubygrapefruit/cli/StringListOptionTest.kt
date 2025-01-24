package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringListOptionTest : AbstractActionTest() {
    @Test
    fun `action can have option with list value`() {
        class Parameter : Action() {
            val param by option("o", "opt").repeated()
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(::Parameter, listOf("-o", "abc")) { action ->
            assertEquals(listOf("abc"), action.param)
        }
        parse(::Parameter, listOf("-o", "a", "--opt", "b", "--opt", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.param)
        }
        parse(::Parameter, listOf("--opt", "a", "--opt", "a")) { action ->
            assertEquals(listOf("a", "a"), action.param)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class Option : Action() {
            val option by option("o").repeated()
        }

        parseFails(::Option, listOf("-o", "1", "-o"), "Value missing for option -o")
        parseFails(::Option, listOf("-o", "1", "-o", "-o", "2"), "Value missing for option -o")
    }

    @Test
    fun `option name must not start with punctuation`() {
        class Broken1 : Action() {
            val option by option("-o").repeated()
        }
        try {
            Broken1()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-o cannot be used as an option name", e.message)
        }
        class Broken2 : Action() {
            val option by option("o", "--option").repeated()
        }
        try {
            Broken2()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--option cannot be used as an option name", e.message)
        }
    }
}