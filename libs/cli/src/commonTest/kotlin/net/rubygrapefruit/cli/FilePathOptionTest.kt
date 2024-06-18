package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FilePathOptionTest : AbstractActionTest() {
    @Test
    fun `can define path option with long name`() {
        class Option : Action() {
            val option by option("opt").path()
        }

        parse(Option(), listOf("--opt", "a/b")) { action ->
            assertEquals(FilePath("a/b"), action.option)
        }

        parseFails(Option(), listOf("-opt"), "Unknown option: -opt")
    }

    @Test
    fun `can define path option with short name`() {
        class Option : Action() {
            val option by option("o").path()
        }

        parse(Option(), listOf("-o", "..")) { action ->
            assertEquals(FilePath(".."), action.option)
        }

        parseFails(Option(), listOf("--o"), "Unknown option: --o")
    }

    @Test
    fun `value is null when option not provided`() {
        class Option : Action() {
            val option by option("opt").path()
        }

        parse(Option(), emptyList()) { action ->
            assertNull(action.option)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class Option : Action() {
            val option by option("o").path()
        }

        parseFails(Option(), listOf("-o"), "Value missing for option -o")
    }
}