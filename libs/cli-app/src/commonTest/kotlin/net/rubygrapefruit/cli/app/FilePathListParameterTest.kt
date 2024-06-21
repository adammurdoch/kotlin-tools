package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathListParameterTest : AbstractActionTest() {
    private val files = FilesFixture()

    @Test
    fun `action can have parameter with list value`() {
        val file1 = files.file("a")
        val file2 = files.file("b")
        val dir1 = files.dir("c/d")

        class Parameter : CliAction() {
            val param by path().parameters("value")
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(Parameter(), listOf(file1.absolutePath)) { action ->
            assertEquals(listOf(file1.path), action.param)
        }
        parse(Parameter(), listOf(file1.absolutePath, file2.absolutePath, dir1.absolutePath)) { action ->
            assertEquals(listOf(file1.path, file2.path, dir1.path), action.param)
        }
    }
}