package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathOptionTest : AbstractActionTest() {
    private val files = FilesFixture()

    @Test
    fun `can define path option with long name`() {
        val file1 = files.file("a")
        val dir1 = files.dir("c/d")
        dir1.createDirectories()
        class Option : CliAction() {
            val option by path().option("opt")
        }

        parse(Option(), listOf("--opt", file1.absolutePath)) { action ->
            assertEquals(file1.path, action.option)
        }
        parse(Option(), listOf("--opt", dir1.absolutePath)) { action ->
            assertEquals(dir1.path, action.option)
        }

        parseFails(Option(), listOf("-opt"), "Unknown option: -opt")
    }
}