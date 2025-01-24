package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class IntListOptionTest : AbstractActionTest() {
    @Test
    fun `action can have option with list value`() {
        class Parameter : Action() {
            val param by int().option("o", "opt").repeated()
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(::Parameter, listOf("-o", "123")) { action ->
            assertEquals(listOf(123), action.param)
        }
        parse(::Parameter, listOf("-o", "1", "--opt", "2", "--opt", "3")) { action ->
            assertEquals(listOf(1, 2, 3), action.param)
        }
        parse(::Parameter, listOf("--opt", "1", "--opt", "1")) { action ->
            assertEquals(listOf(1, 1), action.param)
        }
    }
}