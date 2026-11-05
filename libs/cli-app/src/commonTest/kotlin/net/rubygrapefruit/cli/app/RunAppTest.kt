package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.parser.allOf
import net.rubygrapefruit.cli.parser.parameter
import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class RunAppTest {
    val app = object : Runnable {
        override fun run() {
        }
    }

    @Test
    fun `parses args and runs app and exits with 0`() {
        val output = BufferingOutput()

        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> app }

        assertEquals(0, runApp(parser, listOf("one", "two"), output))
        assertEquals("", output.text)
    }

    @Test
    fun `reports error and exits with 1 when parsing fails`() {
        val output = BufferingOutput()

        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> app }

        assertEquals(1, runApp(parser, listOf("one"), output))
        assertEquals("""Parameter <b> not provided

USAGE: <a> <b>
""", output.text)
    }

    @Test
    fun `reports error and exits with 1 when app throws exception`() {
        val app = object : Runnable {
            override fun run() {
                throw RuntimeException("<broken>")
            }
        }
        val output = BufferingOutput()

        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> app }

        assertEquals(1, runApp(parser, listOf("one", "two"), output))
        assertEquals("""<broken>
""", output.text)
    }

    @Test
    fun `reports exception with no message`() {
        val exception = RuntimeException()
        val app = object : Runnable {
            override fun run() {
                throw exception
            }
        }
        val output = BufferingOutput()

        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> app }

        assertEquals(1, runApp(parser, listOf("one", "two"), output))
        assertEquals("""$exception (no message)
""", output.text)
    }
}