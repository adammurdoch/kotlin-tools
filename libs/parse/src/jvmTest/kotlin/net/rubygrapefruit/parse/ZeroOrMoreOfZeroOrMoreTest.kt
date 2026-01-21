package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class ZeroOrMoreOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of zero or more of one of byte`() {
        val parser = zeroOrMore(
            zeroOrMore(
                oneOf(0x1, 0x2)
            )
        )

        parser.expecting {
            emptyMatch()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        parser.matches(expected = listOf(emptyList()))
        parser.matches(0x2, expected = listOf(bytes(0x2), emptyList()))
        parser.matches(0x2, 0x1, 0x2, expected = listOf(bytes(0x2, 0x1, 0x2), emptyList()))

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x2, 0x1, 0x3, 0x2) {
            failAt(2)
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }
}