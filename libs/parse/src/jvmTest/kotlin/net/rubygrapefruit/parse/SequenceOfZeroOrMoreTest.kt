package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class SequenceOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches zero or more literals then literal with common prefix`() {
        val parser = sequence(
            zeroOrMore(literal("abc", 1)),
            literal("ad", 2)
        ) { a, b ->
            a + b
        }

        parser.matches("ad", expected = listOf(2))
        parser.matches("abcad", expected = listOf(1, 2))
        parser.matches("abcabcad", expected = listOf(1, 1, 2))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("abca") {
            failAt(3)
            expectLiteral("abc")
            expectLiteral("ad")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("aX") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("abcaX") {
            failAt(3)
            expectLiteral("abc")
            expectLiteral("ad")
        }

        // extra
        parser.doesNotMatch("adX") {
            failAt(2)
            expectEndOfInput()
        }
        parser.doesNotMatch("abcadX") {
            failAt(5)
            expectEndOfInput()
        }
    }
}