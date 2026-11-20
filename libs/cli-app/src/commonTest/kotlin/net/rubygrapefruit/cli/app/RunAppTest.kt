package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.parser.params
import net.rubygrapefruit.cli.parser.positional
import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class RunAppTest {
    val app = object : Runnable {
        override fun Runnable.Context.run() {
        }
    }

    @Test
    fun `parses args and runs app and exits with 0`() {
        val output = BufferingOutput()

        val parser = params(positional("a"), positional("b")) { _, _ -> app }

        assertEquals(0, runApp(listOf("one", "two"), output, { parser }))
        assertEquals("", output.text)
    }

    @Test
    fun `reports error and exits with 1 when parsing fails`() {
        val output = BufferingOutput()

        val parser = params(positional("a"), positional("b")) { _, _ -> app }

        assertEquals(1, runApp(listOf("one"), output, { parser }))
        assertEquals(
            """Expected parameter <b>

USAGE: <a> <b>

""", output.text
        )
    }

    @Test
    fun `reports error and exits with 1 when app throws exception`() {
        val app = object : Runnable {
            override fun Runnable.Context.run() {
                throw RuntimeException("<broken>")
            }
        }
        val output = BufferingOutput()

        val parser = params(positional("a"), positional("b")) { _, _ -> app }

        assertEquals(1, runApp(listOf("one", "two"), output, { parser }))
        assertEquals(
            """<broken>
""", output.text
        )
    }

    @Test
    fun `reports exception with no message`() {
        val exception = RuntimeException()
        val app = object : Runnable {
            override fun Runnable.Context.run() {
                throw exception
            }
        }
        val output = BufferingOutput()

        val parser = params(positional("a"), positional("b")) { _, _ -> app }

        assertEquals(1, runApp(listOf("one", "two"), output, { parser }))
        assertEquals(
            """$exception (no message)
""", output.text
        )
    }
}