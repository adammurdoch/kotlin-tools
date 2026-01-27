package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.decide
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class DecideOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `produces literal parser from zero or more of one of byte`() {
        val parser = decide(zeroOrMore(oneOf(0x1, 0x2))) { a -> literal(byteArrayOf(0x3) + a.toByteArray(), a.size) }

        parser.expecting {
            expectDecide {
                expectZeroOrMoreSingleInput(0x1, 0x2)
            }
        }

        parser.matches(0x3, expected = 0)
        parser.matches(0x1, 0x3, 0x1, expected = 1)
        parser.matches(0x1, 0x1, 0x1, 0x3, 0x1, 0x1, 0x1, expected = 3)
        parser.matches(0x2, 0x1, 0x2, 0x3, 0x2, 0x1, 0x2, expected = 3)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
        }
        parser.doesNotMatch(0x2, 0x3) {
            failAt(2)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x2, 0x1, 0x3, 0x2) {
            failAt(4)
            expectLiteral(0x1)
        }

        // unexpected
        parser.doesNotMatch(0x2, 0x3, 0x1) {
            failAt(2)
            expectLiteral(0x2)
        }

        // extra
        parser.doesNotMatch(0x3, 0) {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch(0x3, 0x1) {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch(0x2, 0x3, 0x2, 0) {
            failAt(3)
            expectEndOfInput()
        }
    }
}