package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class ZeroOrMoreOfDiscardOneOfSetTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of one of char`() {
        val parser = zeroOrMore(
            discard(
                oneOf('a', 'b')
            )
        )

        parser.expecting {
            expectZeroOrMoreSingleInput("a", "b")
        }

        parser.matches("")
        parser.matches("a")
        parser.matches("b")
        parser.matches("baa")

        // unexpected
        parser.doesNotMatch("1") {
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }
        parser.doesNotMatch("ba1") {
            failAt(2)
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }
    }
}