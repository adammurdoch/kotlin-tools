package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fileSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathParameterTest : AbstractActionTest() {
    private val currentDir = fileSystem.currentDirectory.path

    @Test
    fun `action can have file path parameter`() {
        class Parameter : CliAction() {
            val param by path().parameter("value")
        }

        parse(Parameter(), listOf("a/b")) { action ->
            assertEquals(currentDir.resolve("a/b"), action.param)
        }
    }
}