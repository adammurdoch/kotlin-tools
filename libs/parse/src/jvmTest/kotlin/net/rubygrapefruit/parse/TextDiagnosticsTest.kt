package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class TextDiagnosticsTest : AbstractParseTest() {
    @Test
    fun `reports location of failure on first line`() {
        val item = oneOf('a', 'b')
        val delim = sequence(item, literal(",")) { _, _ -> }
        val parser = zeroOrMore(delim)

        parser.doesNotMatch("X,a,b") {
            failAt(0, 1, 1)
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
        val delim = sequence(literal("a"), literal(",")) { _, _ -> }
        val line = sequence(delim, literal("\n")) { _, _ -> }
        val parser = zeroOrMore(line)

        parser.doesNotMatch("a,\na,\naX") {
            failAt(7, 3, 2)
            expectLiteral(",")
        }
    }

    @Test
    fun `reports location of failure at end of line`() {
        val delim = sequence(literal("a"), literal(",")) { _, _ -> }
        val line = sequence(delim, literal("\n")) { _, _ -> }
        val parser = zeroOrMore(line)

        parser.doesNotMatch("a,\na,\na") {
            failAt(7, 3, 2)
            expectLiteral(",")
        }
    }
}