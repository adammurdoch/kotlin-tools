package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class ChoiceOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches choice of zero or more of a set of bytes`() {
        val parser = oneOf(
            zeroOrMore(oneOf(0x1, 0x2)),
            zeroOrMore(oneOf(0x10, 0x11))
        )

        parser.expecting {
            emptyMatch()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x10)
            expectLiteral(0x11)
            expectIsChoice(4)
        }

        parser.matches(expected = emptyList())
        parser.matches(0x1, expected = bytes(0x1))
        parser.matches(0x1, 0x2, 0x1, expected = bytes(0x1, 0x2, 0x1))

        // second zero or more can never succeed as first zero or more always succeeds
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
            emptyMatch()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x10)
            expectLiteral(0x12)
            expectIsChoice(4)
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