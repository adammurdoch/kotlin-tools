package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

class ChoiceOfCommandOrNoParametersTest : AbstractParseTest() {
    @Test
    fun `parses one of literal and positional`() {
        val parser = oneOf(
            params(command("one")) { 1 },
            params { 2 }
        )

        assertEquals(1, parser.parse("one").get())
        assertEquals(2, parser.parse().get())
    }

    @Test
    fun `only last choice can match no arguments`() {
        val empty = params { fail() }
        val nonEmpty = params(command("one")) { fail() }

        assertFailsWith<IllegalArgumentException> {
            oneOf(empty, nonEmpty)
        }
    }
}