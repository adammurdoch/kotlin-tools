package net.reubgrapefruit.process

import net.rubygrapefruit.file.fileSystem
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.process.Process
import kotlin.test.*

class ProcessBuilderTest {
    @Test
    fun `can run command`() {
        Process.start(listOf("pwd")).waitFor()
        Process.start(listOf("ls")).waitFor()
    }

    @Test
    fun `throws exception when command exits with non-zero`() {
        val process = Process.start(listOf("ls", "missing"))
        try {
            process.waitFor()
            fail()
        } catch (e: IOException) {
            // Expected
        }

        val process2 = Process.command(listOf("ls", "missing")).startAndCollectOutput()
        try {
            process2.waitFor()
            fail()
        } catch (e: IOException) {
            // Expected
        }
    }

    @Test
    fun `can run command and get exit code`() {
        val pwd = Process.command(listOf("pwd")).startAndGetExitCode().waitFor()
        assertEquals(0, pwd)

        val ls = Process.command(listOf("ls")).startAndGetExitCode().waitFor()
        assertEquals(0, ls)

        val ls2 = Process.command(listOf("ls", "missing")).startAndGetExitCode().waitFor()
        assertNotEquals(0, ls2)
    }

    @Test
    fun `can run command and collect output`() {
        val pwd = Process.command(listOf("pwd")).startAndCollectOutput().waitFor()
        assertEquals(fileSystem.currentDirectory.absolutePath, pwd.trim())

        val ls = Process.command(listOf("ls")).startAndCollectOutput().waitFor()
        println("ls output: $ls")
        assertTrue(ls.isNotEmpty())
    }
}