package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.ParseException
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test
import kotlin.test.assertEquals

class TextDiagnosticsTest : AbstractParseTest() {
    @Test
    fun `reports location of failure for empty input`() {
        val parser = oneOf('a', 'b')

        parser.doesNotMatch("") {
            failAt(0, 1, 1)
            expectContext("", "")
            expectLiteral("a")
            expectLiteral("b")
        }

        // check formatting
        parser.failsWith(
            "", """
1 | 
    ^
Expected "a", "b"
        """.trimIndent()
        )
    }

    @Test
    fun `reports location of failure on only line`() {
        val item = oneOf('a', 'b')
        val parser = zeroOrMore(item, separator = literal(","))

        parser.doesNotMatch("X,a,b") {
            failAt(0, 1, 1)
            expectContext("", "X,a,b")
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }

        parser.failsWith(
            "a,b,X,\n,b", """
1 | a,b,X,
        ^
Expected "a", "b"
""".trimIndent()
        )
    }

    @Test
    fun `reports location of failure on first line`() {
        val item = oneOf('a', 'b')
        val parser = zeroOrMore(item, separator = literal(","))

        parser.doesNotMatch("a,b,X,\n,b") {
            failAt(4, 1, 5)
            expectLiteral("a")
            expectLiteral("b")
            expectContext("a,b,X", ",")
        }

        parser.failsWith(
            "a,b,X,\n,b", """
1 | a,b,X,
        ^
Expected "a", "b"
""".trimIndent()
        )
    }

    @Test
    fun `reports location of failure on subsequent line`() {
        val item = sequence(literal("a", 1), literal(","))
        val line = sequence(item, literal("\n"))
        val parser = zeroOrMore(line)

        parser.doesNotMatch("a,\na,\naXX") {
            failAt(7, 3, 2)
            expectContext("a", "XX")
            expectLiteral(",")
        }

        parser.failsWith("a,\na,\naXX", """
3 | aXX
     ^
Expected ","
        """.trimIndent())
    }

    @Test
    fun `reports location of failure at end of line`() {
        val item = sequence(literal("a", 1), literal(","))
        val line = sequence(item, literal("\n"))
        val parser = zeroOrMore(line)

        parser.doesNotMatch("a,\na\na,") {
            failAt(4, 2, 2)
            expectContext("a", "")
            expectLiteral(",")
        }

        parser.failsWith("a,\na\na,", """
2 | a
     ^
Expected ","
        """.trimIndent())
    }

    @Test
    fun `reports location of failure at end of input`() {
        val item = sequence(literal("a", 1), literal(","))
        val line = sequence(item, literal("\n"))
        val parser = sequence(line, line) { _, _ -> 1 }

        parser.doesNotMatch("a,\na") {
            failAt(4, 2, 2)
            expectContext("a", "")
            expectLiteral(",")
        }

        parser.failsWith("a,\na", """
2 | a
     ^
Expected ","
        """.trimIndent())
    }

    private fun Parser<TextInput, *>.failsWith(input: String, errorMessage: String) {
        try {
            parse(input).get()
        } catch (e: ParseException) {
            assertEquals(errorMessage, e.message)
        }
    }
}