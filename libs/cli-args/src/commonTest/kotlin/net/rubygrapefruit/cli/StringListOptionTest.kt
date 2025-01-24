package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringListOptionTest : AbstractActionTest() {
    @Test
    fun `action can have option with list value`() {
        class Parameter : Action() {
            val param by options("o", "opt")
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
    fun `option name must not start with punctuation`() {
        class Broken1 : Action() {
            val option by options("-o")
        }
        try {
            Broken1()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("-o cannot be used as an option name", e.message)
        }
        class Broken2 : Action() {
            val option by options("o", "--option")
        }
        try {
            Broken2()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--option cannot be used as an option name", e.message)
        }
    }
}