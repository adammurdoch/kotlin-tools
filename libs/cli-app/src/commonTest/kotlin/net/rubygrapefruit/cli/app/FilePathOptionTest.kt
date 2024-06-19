package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.FilePath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FilePathOptionTest : AbstractActionTest() {
    @Test
    fun `can define path option with long name`() {
        class Option : Action() {
            val option by path().option("opt")
        }

        parse(Option(), listOf("--opt", "a/b")) { action ->
            assertEquals(FilePath("a/b"), action.option)
        }

        parseFails(Option(), listOf("-opt"), "Unknown option: -opt")
    }

    @Test
    fun `can define path option with short name`() {
        class Option : Action() {
            val option by path().option("o")
        }

        parse(Option(), listOf("-o", "")) { action ->
            assertEquals(FilePath(""), action.option)
        }

        parseFails(Option(), listOf("--o"), "Unknown option: --o")
    }

    @Test
    fun `value is null when option not provided`() {
        class Option : Action() {
            val option by path().option("opt")
        }

        parse(Option(), emptyList()) { action ->
            assertNull(action.option)
        }
    }

    @Test
    fun `fails when argument not provided`() {
        class Option : Action() {
            val option by path().option("o")
        }

        parseFails(Option(), listOf("-o"), "Value missing for option -o")
    }
}