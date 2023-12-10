package net.rubygrapefruit.process

import net.rubygrapefruit.file.fileSystem
import net.rubygrapefruit.file.fixtures.FilesFixture
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.stream.CollectingBuffer
import kotlin.test.*

class ProcessBuilderTest {
    private val fixture = FilesFixture()

    @AfterTest
    fun cleanup() {
        fixture.testDir.deleteRecursively()
    }

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

        val process2 = Process.command(listOf("ls", "missing")).collectOutput().start()
        try {
            process2.waitFor()
            fail()
        } catch (e: IOException) {
            // Expected
        }
    }

    @Test
    fun `can run command and get exit code`() {
        val pwd = Process.command(listOf("pwd")).collectExitCode().start().waitFor()
        assertEquals(0, pwd)

        val ls = Process.command(listOf("ls")).collectExitCode().start().waitFor()
        assertEquals(0, ls)

        val ls2 = Process.command(listOf("ls", "missing")).collectExitCode().start().waitFor()
        assertNotEquals(0, ls2)
    }

    @Test
    fun `can run command and collect output`() {
        val pwd = Process.command(listOf("pwd")).collectOutput().start().waitFor()
        assertEquals(fileSystem.currentDirectory.absolutePath, pwd.trim())

        val dir = fixture.dir("test") {
            file("1")
            file("2")
        }

        val ls = Process.command(listOf("ls", dir.absolutePath)).collectOutput().start().waitFor()
        assertEquals(listOf("1", "2"), ls.trim().lines().sorted())
    }

    @Test
    fun `can run command in directory`() {
        val dir = fixture.dir("test") {
            file("1")
            file("2")
        }

        val pwd = Process.command(listOf("pwd")).directory(dir).collectOutput().start().waitFor()
        assertEquals(dir.absolutePath, pwd.trim())

        val ls = Process.command(listOf("ls")).directory(dir).collectOutput().start().waitFor()
        assertEquals(listOf("1", "2"), ls.trim().lines().sorted())
    }

    @Test
    fun `can run command and act on input and output`() {
        val result = Process.command("head", "-n", "1").withInputAndOutput { read, write ->
            write.write("greetings\nignore this\n".encodeToByteArray())
            val buffer = CollectingBuffer()
            buffer.readFrom(read)
            buffer.decodeToString()
        }.start().waitFor()

        assertEquals("greetings\n", result)
    }
}