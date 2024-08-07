package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class DirectoryParameterTest : AbstractActionTest() {
    private val files = FilesFixture()

    @Test
    fun `action can have directory parameter`() {
        class Parameter : CliAction() {
            val param by dir().parameter("value")
        }

        val dir = files.dir("a/b")

        parse(Parameter(), listOf(dir.absolutePath)) { action ->
            assertEquals(dir.path, action.param.path)
        }
    }

    @Test
    fun `fails when path does not exist`() {
        class Parameter : CliAction() {
            val param by dir().parameter("value")
        }

        val path = files.testDir.path.resolve("a/b")

        parseFails(Parameter(), listOf(path.absolutePath), "Value for parameter 'value' is not a directory: $path")
    }

    @Test
    fun `can accept path that does not exist`() {
        class Parameter : CliAction() {
            val param by dir(mustExist = false).parameter("value")
        }

        val path = files.testDir.path.resolve("a/b")

        parse(Parameter(), listOf(path.absolutePath)) { action ->
            assertEquals(path, action.param.path)
        }
    }

    @Test
    fun `fails when path is a regular file`() {
        class Parameter : CliAction() {
            val param by dir().parameter("value")
        }

        val file = files.file("a/b")

        parseFails(Parameter(), listOf(file.absolutePath), "Value for parameter 'value' is not a directory: $file")
    }
}