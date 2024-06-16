package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IntOptionTest : AbstractActionTest() {
    @Test
    fun `can define int option with long name`() {
        class IntOption : Action() {
            val option by option("opt").int()
        }

        parse(IntOption(), listOf("--opt", "123")) { action ->
            assertEquals(123, action.option)
        }

        parseFails(IntOption(), listOf("-opt"), "Unknown option: -opt")
    }

    @Test
    fun `can define int option with short name`() {
        class IntOption : Action() {
            val option by option("o").int()
        }

        parse(IntOption(), listOf("-o", "123")) { action ->
            assertEquals(123, action.option)
        }

        parseFails(IntOption(), listOf("--o"), "Unknown option: --o")
    }

    @Test
    fun `value is null when option not provided`() {
        class IntOption : Action() {
            val option by option("opt").int()
        }

        parse(IntOption(), emptyList()) { action ->
            assertNull(action.option)
        }
    }

    @Test
    fun `can provide default value for int option`() {
        class IntOption : Action() {
            val option by option("opt").int().whenAbsent(45)
        }

        parse(IntOption(), emptyList()) { action ->
            assertEquals(45, action.option)
        }

        parse(IntOption(), listOf("--opt", "123")) { action ->
            assertEquals(123, action.option)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class IntOption : Action() {
            val option by option("o").int()
        }

        parseFails(IntOption(), listOf("-o"), "Value missing for option -o")
    }

    @Test
    fun `fails when argument is not an integer`() {
        class IntOption : Action() {
            val option by option("o").int()
        }

        parseFails(IntOption(), listOf("-o", "abc"), "Argument for option -o is not an integer: abc")
    }
}