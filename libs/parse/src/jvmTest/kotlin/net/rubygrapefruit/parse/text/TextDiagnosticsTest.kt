package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.combinators.prefixed
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

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
    }

    @Test
    fun `reports location of failure on first line`() {
        val item = oneOf('a', 'b')
        val delim = prefixed(item, literal(","))
        val parser = zeroOrMore(delim)

        parser.doesNotMatch("X,a,b") {
            failAt(0, 1, 1)
            expectContext("", "X,a,b")
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }

        parser.doesNotMatch("a,b,X,b") {
            failAt(4, 1, 5)
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }
    }

    @Test
    fun `reports location of failure on subsequent line`() {
        val delim = sequence(literal("a", 1), literal(","))
        val line = sequence(delim, literal("\n"))
        val parser = zeroOrMore(line)

        parser.doesNotMatch("a,\na,\naXX") {
            failAt(7, 3, 2)
            expectContext("a", "XX")
            expectLiteral(",")
        }
    }

    @Test
    fun `reports location of failure at end of line`() {
        val delim = sequence(literal("a", 1), literal(","))
        val line = sequence(delim, literal("\n"))
        val parser = zeroOrMore(line)

        parser.doesNotMatch("a,\na\na,") {
            failAt(4, 2, 2)
            expectContext("a", "")
            expectLiteral(",")
        }
    }

    @Test
    fun `reports location of failure at end of input`() {
        val delim = sequence(literal("a", 1), literal(","))
        val line = sequence(delim, literal("\n"))
        val parser = sequence(line, line) { _, _ -> 1 }

        parser.doesNotMatch("a,\na") {
            failAt(4, 2, 2)
            expectContext("a", "")
            expectLiteral(",")
        }
    }
}