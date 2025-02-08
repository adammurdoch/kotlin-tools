package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class StringListOptionTest : AbstractActionTest() {
    @Test
    fun `action can have option with list value`() {
        class Option : Action() {
            val option by option("o", "opt").repeated()
        }

        parse(::Option, emptyList()) { action ->
            assertEquals(emptyList(), action.option)
        }
        parse(::Option, listOf("-o", "abc")) { action ->
            assertEquals(listOf("abc"), action.option)
        }
        parse(::Option, listOf("-o", "a", "--opt", "b", "--opt", "c")) { action ->
            assertEquals(listOf("a", "b", "c"), action.option)
        }
        parse(::Option, listOf("--opt", "a", "--opt", "a")) { action ->
            assertEquals(listOf("a", "a"), action.option)
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
}