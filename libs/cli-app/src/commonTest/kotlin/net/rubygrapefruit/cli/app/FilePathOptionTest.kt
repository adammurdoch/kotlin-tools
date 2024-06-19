package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fileSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathOptionTest : AbstractActionTest() {
    private val currentDir = fileSystem.currentDirectory.path

    @Test
    fun `can define path option with long name`() {
        class Option : CliAction() {
            val option by path().option("opt")
        }

        parse(Option(), listOf("--opt", "a/b")) { action ->
            assertEquals(currentDir.resolve("a/b"), action.option)
        }

        parseFails(Option(), listOf("-opt"), "Unknown option: -opt")
    }
}