package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class ChoiceOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches choice of zero or more byte literals`() {
        val parser = oneOf(
            zeroOrMore(literal(byteArrayOf(0x1, 0x2), 1)),
            zeroOrMore(literal(byteArrayOf(0x11, 0x12), 2))
        )

        parser.expecting {
            expectChoice {
                expectOneOrMore {
                    expectLiteral(0x1, result = 1)
                }
                expectZero()
                // should discard everything after the second choice
                expectOneOrMore {
                    expectLiteral(0x11, result = 2)
                }
                expectZero()
            }
        }

        parser.matches(expected = emptyList())
        parser.matches(0x1, 0x2, expected = listOf(1))
        parser.matches(0x1, 0x2, 0x1, 0x2, expected = listOf(1, 1))

        // second zero or more can never succeed as first always succeeds
        parser.doesNotMatch(0x11, 0x12) {
            expectEndOfInput()
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x11, 0x12, 0x11, 0x12) {
            expectEndOfInput()
            expectLiteral(0x1)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1, 0x3) {
            failAt(1)
            expectLiteral(0x2)
        }
    }

    @Test
    fun `matches choice of zero or more of a set of bytes`() {
        val parser = oneOf(
            zeroOrMore(oneOf(0x1, 0x2)),
            zeroOrMore(oneOf(0x10, 0x11))
        )

        parser.expecting {
            expectChoice {
                expectZeroOrMoreSingleInput(0x1, 0x2)
                expectZeroOrMoreSingleInput(0x10, 0x11)
            }
        }

        parser.matches(expected = emptyList())
        parser.matches(0x1, expected = bytes(0x1))
        parser.matches(0x1, 0x2, 0x1, expected = bytes(0x1, 0x2, 0x1))

        // second zero or more can never succeed as first always succeeds
        parser.doesNotMatch(0x11) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x11, 0x10, 0x11) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x11) {
            failAt(1)
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }

    @Test
    fun `matches choice of zero or more of a set of bytes or choice of literals`() {
        val parser = oneOf(
            oneOf(
                literal(byteArrayOf(0x10, 0x11), bytes(0x10, 0x11)),
                literal(byteArrayOf(0x12), bytes(0x12))
            ),
            zeroOrMore(oneOf(0x1, 0x2)),
        )

        parser.expecting {
            expectChoice {
                expectLiteral(0x10, 0x11, result = bytes(0x10, 0x11))
                expectLiteral(0x12, result = bytes(0x12))
                expectZeroOrMoreSingleInput(0x1, 0x2)
            }
        }

        parser.matches(expected = emptyList())
        parser.matches(0x1, expected = bytes(0x1))
        parser.matches(0x1, 0x2, 0x1, expected = bytes(0x1, 0x2, 0x1))
        parser.matches(0x10, 0x11, expected = bytes(0x10, 0x11))
        parser.matches(0x12, expected = bytes(0x12))

        // missing
        parser.doesNotMatch(0x10) {
            failAt(1)
            expectLiteral(0x11)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x10)
            expectLiteral(0x12)
        }
        parser.doesNotMatch(0x10, 0x3) {
            failAt(1)
            expectLiteral(0x11)
        }
    }
}