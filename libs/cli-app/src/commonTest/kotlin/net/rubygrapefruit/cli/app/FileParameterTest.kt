package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class FileParameterTest : AbstractActionTest() {
    private val files = FilesFixture()

    @Test
    fun `action can have file parameter`() {
        class Parameter : CliAction() {
            val param by file().parameter("value")
        }

        val file = files.file("a/b")

        parse(Parameter(), listOf(file.absolutePath)) { action ->
            assertEquals(file.path, action.param.path)
        }
    }

    @Test
    fun `fails when path does not exist`() {
        class Parameter : CliAction() {
            val param by file().parameter("value")
        }

        val path = files.testDir.path.resolve("a/b")

        parseFails(Parameter(), listOf(path.absolutePath), "Value for parameter 'value' is not a file: $path")
    }

    @Test
    fun `can accept path that does not exist`() {
        class Parameter : CliAction() {
            val param by file(mustExist = false).parameter("value")
        }

        val path = files.testDir.path.resolve("a/b")

        parse(Parameter(), listOf(path.absolutePath)) { action ->
            assertEquals(path, action.param.path)
        }
    }

    @Test
    fun `fails when path is a directory`() {
        class Parameter : CliAction() {
            val param by file().parameter("value")
        }

        val dir = files.dir("a/b")

        parseFails(Parameter(), listOf(dir.absolutePath), "Value for parameter 'value' is not a file: $dir")
    }
}