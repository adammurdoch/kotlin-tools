package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

/**
 * Some cross-cutting tests, most error handling is covered by the parser specific tests.
 */
class ParseFailureTest : AbstractParseTest() {
    @Test
    fun `reports problem and shows usage summary on parse failure`() {
        val parser = params(option("a"), option("b")) { _, _ -> fail() }

        val result = parser.parse("-a", "--unknown")
        assertIs<Parser.Failure>(result)

        assertEquals(2, result.problems.size)

        val output = BufferingOutput()
        result.writeTo(output)
        assertEquals(
            """No value provided for option -a
Unexpected option '--unknown'

USAGE: [-a <value>] [-b <value>]
""", output.text
        )
    }
}