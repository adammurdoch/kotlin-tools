package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class OneOrMoreStringPositionalAndCommandTest : AbstractParseTest() {
    @Test
    fun `repeated positional is reluctant`() {
        val parser = params(positional("a").oneOrMore(), command("end")) { a, _ -> a }

        assertEquals(listOf("one"), parser.parse("one", "end").get())
        assertEquals(listOf("one", "two"), parser.parse("one", "two", "end").get())

        assertEquals(listOf("end"), parser.parse("end", "end").get())
    }

    @Test
    fun `parameter and command must be provided`() {
        val parser = params(positional("a").oneOrMore(), command("end")) { a, _ -> a }

        parser.fails {
            expectedPositional(0, "a")
        }
        parser.fails("one") {
            expectedCommand(1, "end")
        }
    }
}