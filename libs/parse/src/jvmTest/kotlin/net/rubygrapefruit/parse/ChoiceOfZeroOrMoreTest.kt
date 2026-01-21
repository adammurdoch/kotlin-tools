package net.rubygrapefruit.parse

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
        }

        parser.matches(expected = emptyList())
        parser.matches(0x1, expected = listOf(0x1))
        parser.matches(0x1, 0x2, 0x1, expected = listOf(0x1, 0x2, 0x1))

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
}