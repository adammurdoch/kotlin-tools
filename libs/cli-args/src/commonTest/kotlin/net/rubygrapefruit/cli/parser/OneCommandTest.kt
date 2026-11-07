package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class OneCommandTest : AbstractParseTest() {
    @Test
    fun `parses command`() {
        val parser = params(command("run")) { "result" }

        assertEquals("result", parser.parse("run").get())
    }
}