package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathParameterTest : AbstractActionTest() {
    private val files = FilesFixture()

    @Test
    fun `action can have file path parameter that accepts files and directories that exist`() {
        class Parameter : CliAction() {
            val param by path().parameter("value")
        }

        val file1 = files.file("a")
        val dir1 = files.dir("c/d")

        parse(Parameter(), listOf(file1.absolutePath)) { action ->
            assertEquals(file1.path, action.param)
        }
        parse(Parameter(), listOf(dir1.absolutePath)) { action ->
            assertEquals(dir1.path, action.param)
        }
    }

    @Test
    fun `fails when path references something that does not exist`() {
        class Parameter : CliAction() {
            val param by path().parameter("value")
        }

        parseFails(Parameter(), listOf("nothing"), "Value for parameter 'value' does not exist: nothing")
    }

    @Test
    fun `can accept path that does not exist`() {
        class Parameter : CliAction() {
            val param by path(mustExist = false).parameter("value")
        }

        val path = files.testDir.path.resolve("a/b")

        parse(Parameter(), listOf(path.absolutePath)) { action ->
            assertEquals(path, action.param)
        }
    }
}