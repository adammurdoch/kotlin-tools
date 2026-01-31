package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.decide
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class CharMatchOfDecideTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of char followed by literal`() {
        val parser = match(
            decide(
                zeroOrMore(oneOf('a', 'b'))
            ) { a ->
                if (a.isEmpty()) {
                    literal("!", 0)
                } else {
                    literal(a.joinToString("").uppercase(), 12)
                }
            }
        )

        parser.expecting {
            expectMatch {
                expectDecide {
                    expectZeroOrMoreSingleInput { parser
                        expectOneOf("a", "b")
                    }
                    expectLiteral("!")
                }
            }
        }

        parser.matches("!", expected = "!")
        parser.matches("bB", expected = "bB")
        parser.matches("baaBAA", expected = "baaBAA")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("!")
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("b") {
            failAt(1)
            expectLiteral("B")
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("baB") {
            failAt(2)
            expectLiteral("BA")
            expectLiteral("a")
            expectLiteral("b")
        }

        // unexpected
        parser.doesNotMatch("aX") {
            failAt(1)
            expectLiteral("A")
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("abAX") {
            failAt(2)
            expectLiteral("AB")
            expectLiteral("a")
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("X") {
            expectLiteral("!")
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("baBAX") {
            failAt(4)
            expectEndOfInput()
        }
    }
}