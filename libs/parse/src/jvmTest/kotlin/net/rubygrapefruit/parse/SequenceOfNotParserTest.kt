package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.not
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class SequenceOfNotParserTest : AbstractParseTest() {
    @Test
    fun `matches not single byte literal followed by bytes`() {
        val parser = sequence(
            not(literal(byteArrayOf(0x1))),
            zeroOrMore(oneOf(0x1, 0x2))
        )

        parser.expecting {
            emptyMatch()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        parser.matches(expected = emptyList())
        parser.matches(0x2, expected = listOf(0x2))
        parser.matches(0x2, 0x1, expected = listOf(0x2, 0x1))

        parser.doesNotMatch(0x1) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x2) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }

    @Test
    fun `matches not multi-byte literal followed by bytes`() {
        val parser = sequence(
            not(literal(byteArrayOf(0x1, 0x2))),
            zeroOrMore(oneOf(0x1, 0x2))
        )

        parser.expecting {
            emptyMatch()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        parser.matches(expected = emptyList())
        parser.matches(0x2, expected = listOf(0x2))
        parser.matches(0x2, 0x1, expected = listOf(0x2, 0x1))
        parser.matches(0x1, expected = listOf(0x1))
        parser.matches(0x1, 0x1, expected = listOf(0x1, 0x1))
        parser.matches(0x1, 0x1, 0x2, expected = listOf(0x1, 0x1, 0x2))

        parser.doesNotMatch(0x1, 0x2) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }

    @Test
    fun `matches not one of byte followed by bytes`() {
        val parser = sequence(
            not(oneOf(0x1, 0x2)),
            zeroOrMore(oneOf(0x1, 0x2, 0x3, 0x4))
        )

        parser.expecting {
            emptyMatch()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
            expectLiteral(0x4)
        }

        parser.matches(expected = emptyList())
        parser.matches(0x3, expected = listOf(0x3))
        parser.matches(0x3, 0x1, expected = listOf(0x3, 0x1))

        parser.doesNotMatch(0x1) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
            expectLiteral(0x4)
        }
        parser.doesNotMatch(0x2, 0x4) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
            expectLiteral(0x4)
        }

        // unexpected
        parser.doesNotMatch(0x5) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
            expectLiteral(0x4)
        }
    }
}