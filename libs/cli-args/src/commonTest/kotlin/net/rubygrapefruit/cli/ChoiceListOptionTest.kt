package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceListOptionTest : AbstractActionTest() {
    @Test
    fun `action can have option with list value`() {
        class Parameter : Action() {
            val param by oneOf {
                choice(1, "1")
                choice(2, "two")
                choice(3, "3")
            }.option("o", "opt").repeated()
        }

        parse(::Parameter, emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(::Parameter, listOf("-o", "1")) { action ->
            assertEquals(listOf(1), action.param)
        }
        parse(::Parameter, listOf("-o", "1", "--opt", "two", "--opt", "3")) { action ->
            assertEquals(listOf(1, 2, 3), action.param)
        }
        parse(::Parameter, listOf("--opt", "1", "--opt", "1")) { action ->
            assertEquals(listOf(1, 1), action.param)
        }
    }
}