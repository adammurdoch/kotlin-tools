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
            expectSequence {
                expectNot {
                    expectLiteral(0x1)
                }
                expectZeroOrMoreSingleInput(0x1, 0x2)
            }
        }

        parser.matches(expected = emptyList())
        parser.matches(0x2, expected = bytes(0x2))
        parser.matches(0x2, 0x1, expected = bytes(0x2, 0x1))

        // matches not predicate
        parser.doesNotMatch(0x1) {
            expectEndOfInput()
            expect("not x01")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x2) {
            expectEndOfInput()
            expect("not x01")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expect("not x01")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x2, 0x3) {
            failAt(1)
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
            expectSequence {
                expectNot {
                    expectLiteral(0x1, 0x2)
                }
                expectZeroOrMoreSingleInput(0x1, 0x2)
            }
        }

        parser.matches(expected = emptyList())
        parser.matches(0x2, expected = bytes(0x2))
        parser.matches(0x2, 0x1, expected = bytes(0x2, 0x1))
        parser.matches(0x1, expected = bytes(0x1))
        parser.matches(0x1, 0x1, expected = bytes(0x1, 0x1))
        parser.matches(0x1, 0x1, 0x2, expected = bytes(0x1, 0x1, 0x2))

        // matches not predicate
        parser.doesNotMatch(0x1, 0x2) {
            expectEndOfInput()
            expect("not x01")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            expectEndOfInput()
            expect("not x01")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expect("not x01")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x3) {
            failAt(1)
            expectEndOfInput()
            expect("not x02")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x2, 0x3) {
            failAt(1)
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
            expectSequence {
                expectNot {
                    expectOneOf(0x1, 0x2)
                }
                expectZeroOrMoreSingleInput(0x1, 0x2, 0x3, 0x4)
            }
        }

        parser.matches(expected = emptyList())
        parser.matches(0x3, expected = bytes(0x3))
        parser.matches(0x3, 0x1, expected = bytes(0x3, 0x1))

        // matches not predicate
        parser.doesNotMatch(0x1) {
            expectEndOfInput()
            expect("not x01")
            expect("not x02")
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
            expectLiteral(0x4)
        }
        parser.doesNotMatch(0x2, 0x4) {
            expectEndOfInput()
            expect("not x01")
            expect("not x02")
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
            expectLiteral(0x4)
        }

        // unexpected
        parser.doesNotMatch(0x5) {
            expectEndOfInput()
            expect("not x01")
            expect("not x02")
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
            expectLiteral(0x4)
        }
        parser.doesNotMatch(0x3, 0x5) {
            failAt(1)
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
            expectLiteral(0x4)
        }
    }
}