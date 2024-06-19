package net.rubygrapefruit.cli.app

import net.rubygrapefruit.file.fileSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class FilePathListParameterTest : AbstractActionTest() {
    private val currentDir = fileSystem.currentDirectory.path

    @Test
    fun `action can have parameter with list value`() {
        class Parameter : CliAction() {
            val param by path().parameters("value")
        }

        parse(Parameter(), emptyList()) { action ->
            assertEquals(emptyList(), action.param)
        }
        parse(Parameter(), listOf("abc")) { action ->
            assertEquals(listOf(currentDir.resolve("abc")), action.param)
        }
        parse(Parameter(), listOf("a", "b", "c")) { action ->
            assertEquals(listOf(currentDir.resolve("a"), currentDir.resolve("b"), currentDir.resolve("c")), action.param)
        }
    }
}