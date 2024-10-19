package net.rubygrapefruit.process

import kotlinx.io.readString
import kotlinx.io.writeString
import net.rubygrapefruit.file.fixtures.FilesFixture
import net.rubygrapefruit.io.IOException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail


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
        val process = Process.start(listOf(TestApp.path, "fail"))
        try {
            process.waitFor()
            fail()
        } catch (_: IOException) {
            // Expected
        }
    }

    @Test
    fun `can run command and get exit code`() {
        val zero = Process.command(listOf(TestApp.path)).collectExitCode().start().waitFor()
        assertEquals(0, zero)

        val nonZero = Process.command(listOf(TestApp.path, "fail", "12")).collectExitCode().start().waitFor()
        assertEquals(12, nonZero)
    }

    @Test
    fun `can run command and collect output`() {
        val out = Process.command(listOf(TestApp.path, "count", "2")).collectOutput().start().waitFor()
        assertEquals(listOf("1", "2"), out.trim().lines())
    }

    @Test
    fun `throws exception when collecting output and command exits with non-zero`() {
        val process = Process.command(listOf(TestApp.path, "fail")).collectOutput().start()
        try {
            process.waitFor()
            fail()
        } catch (_: IOException) {
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