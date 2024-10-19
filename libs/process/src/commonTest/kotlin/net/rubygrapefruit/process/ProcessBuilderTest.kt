package net.rubygrapefruit.process

import kotlinx.io.readString
import kotlinx.io.writeString
import net.rubygrapefruit.file.fileSystem
import net.rubygrapefruit.file.fixtures.FilesFixture
import net.rubygrapefruit.io.IOException
import kotlin.test.*


expect fun pwd(): List<String>

expect fun ls(): List<String>

class ProcessBuilderTest {
    private val fixture = FilesFixture()

    @AfterTest
    fun cleanup() {
        fixture.testDir.deleteRecursively()
    }

    @Test
    fun `can run command`() {
        Process.start(listOf(TestApp.path)).waitFor()
    }

    @Test
    fun `throws exception when command exits with non-zero`() {
        val process = Process.start(ls() + listOf("missing"))
        try {
            process.waitFor()
            fail()
        } catch (e: IOException) {
            // Expected
        }
    }

    @Test
    fun `can run command and get exit code`() {
        val pwd = Process.command(pwd()).collectExitCode().start().waitFor()
        assertEquals(0, pwd)

        val ls = Process.command(ls()).collectExitCode().start().waitFor()
        assertEquals(0, ls)

        val ls2 = Process.command(ls() + listOf("missing")).collectExitCode().start().waitFor()
        assertNotEquals(0, ls2)
    }

    @Test
    fun `can run command and collect output`() {
        val pwd = Process.command(pwd()).collectOutput().start().waitFor()
        assertEquals(fileSystem.currentDirectory.absolutePath, pwd.trim())

        val dir = fixture.dir("test") {
            file("1")
            file("2")
        }

        val ls = Process.command(listOf("ls", dir.absolutePath)).collectOutput().start().waitFor()
        assertEquals(listOf("1", "2"), ls.trim().lines().sorted())
    }

    @Test
    fun `throws exception when collecting output and command exits with non-zero`() {
        val process = Process.command(ls() + listOf("missing")).collectOutput().start()
        try {
            process.waitFor()
            fail()
        } catch (e: IOException) {
            // Expected
        }
    }

    @Test
    fun `can run command in directory`() {
        val dir = fixture.dir("test") {
            file("1")
            file("2")
        }

        val pwd = Process.command(pwd()).directory(dir).collectOutput().start().waitFor()
        assertEquals(dir.absolutePath, pwd.trim())

        val ls = Process.command(ls()).directory(dir).collectOutput().start().waitFor()
        assertEquals(listOf("1", "2"), ls.trim().lines().sorted())
    }

    @Test
    fun `can run command and act on input and output`() {
        val result = Process.command("head", "-n", "1").withInputAndOutput { read, write ->
            write.writeString("greetings\nignore this\n")
            write.emit()
            read.readString()
        }.start().waitFor()

        assertEquals("greetings\n", result)
    }
}