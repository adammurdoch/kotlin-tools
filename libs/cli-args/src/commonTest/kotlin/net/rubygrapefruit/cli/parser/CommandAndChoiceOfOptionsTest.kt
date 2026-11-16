package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class CommandAndChoiceOfOptionsTest : AbstractParseTest() {
    @Test
    fun `parses command with choice of options`() {
        val parser = params(
            command("run"),
            oneOf(
                option("hostname", "h"),
                option("ip")
            )
        ) { it }

        assertEquals("some-host", parser.parse("run", "-h", "some-host").get())
    }

    @Test
    fun `reports usage of command with choice of options`() {
        val parser = params(
            command("run"),
            oneOf(
                option("hostname", "h"),
                option("ip")
            )
        ) { it }

        parser.usage("run ([-h <value> | --hostname <value>] | [--ip <value>])")

        parser.options {
            option("-h <value>, --hostname <value>")
            option("--ip <value>")
        }

        parser.noPositionals()
    }
}