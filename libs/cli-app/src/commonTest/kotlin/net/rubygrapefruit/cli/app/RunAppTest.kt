package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.parser.allOf
import net.rubygrapefruit.cli.parser.parameter
import kotlin.test.Test
import kotlin.test.assertEquals

class RunAppTest {
    val app = object : Runnable {
        override fun run() {
        }
    }

    @Test
    fun `parses args and runs app and exits with 0`() {
        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> app }

        assertEquals(0, runApp(parser, listOf("one", "two")))
    }

    @Test
    fun `exits with 1 when parsing fails`() {
        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> app }

        assertEquals(1, runApp(parser, listOf("one")))
    }

    @Test
    fun `exits with 1 when app throws exception`() {
        val app = object : Runnable {
            override fun run() {
                throw RuntimeException()
            }
        }
        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> app }

        assertEquals(1, runApp(parser, listOf("one", "twi")))
    }
}