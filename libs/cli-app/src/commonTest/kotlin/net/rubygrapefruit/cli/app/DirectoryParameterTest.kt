package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class DirectoryParameterTest : AbstractActionTest() {
    private val files = FilesFixture()

    @Test
    fun `action can have directory parameter`() {
        val dir = files.testDir.dir("a/b")
        dir.createDirectories()

        class Parameter : CliAction() {
            val param by dir().parameter("value")
        }

        parse(Parameter(), listOf(dir.absolutePath)) { action ->
            assertEquals(dir.path, action.param.path)
        }
    }

    @Test
    fun `fails when path does not exist`() {
        val file = files.testDir.file("a/b")

        class Parameter : CliAction() {
            val param by dir().parameter("value")
        }

        parseFails(Parameter(), listOf(file.absolutePath), "Value for parameter 'value' is not a directory: $file")
    }

    @Test
    fun `fails when path is a regular file`() {
        val file = files.testDir.file("a/b")
        file.parent?.createDirectories()
        file.writeText("content")

        class Parameter : CliAction() {
            val param by dir().parameter("value")
        }

        parseFails(Parameter(), listOf(file.absolutePath), "Value for parameter 'value' is not a directory: $file")
    }
}