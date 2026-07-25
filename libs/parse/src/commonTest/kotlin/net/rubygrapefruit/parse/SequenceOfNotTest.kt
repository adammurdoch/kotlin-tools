package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.general.endOfInput
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.one
import kotlin.test.Test

class SequenceOfNotTest : AbstractParseTest() {
    @Test
    fun `matches not single byte literal followed by zero or more bytes`() {
        val parser = sequence(
            not(literal(byteArrayOf(0x1), 1)),
            zeroOrMore(oneOf(0x1, 0x2))
        )

        parser.expecting {
            expectSequence {
                expectNot {
                    expectLiteral(0x1, result = Unit)
                }
                expectZeroOrMoreSingleInput {
                    expectOneOf(0x1, 0x2)
                }
            }
        }

        parser.matches(expected = emptyList()) {
            steps {
                advance(0)
                advance(0)
            }
        }
        parser.matches(0x2, expected = bytes(0x2)) {
            steps {
                advance(0)
                advance(1)
            }
        }
        parser.matches(0x2, 0x1, expected = bytes(0x2, 0x1)) {
            steps {
                advance(0)
                advance(2)
            }
        }

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
    fun `matches not multi-byte literal followed by zero or more bytes`() {
        val parser = sequence(
            not(literal(byteArrayOf(0x1, 0x2), 1)),
            zeroOrMore(oneOf(0x1, 0x2))
        )

        parser.expecting {
            expectSequence {
                expectNot {
                    expectLiteral(0x1, 0x2)
                }
                expectZeroOrMoreSingleInput {
                    expectOneOf(0x1, 0x2)
                }
            }
        }

        parser.matches(expected = emptyList()) {
            steps {
                advance(0)
                advance(0)
            }
        }
        parser.matches(0x2, expected = bytes(0x2)) {
            steps {
                advance(0)
                advance(1)
            }
        }
        parser.matches(0x2, 0x1, expected = bytes(0x2, 0x1)) {
            steps {
                advance(0)
                advance(2)
            }
        }
        parser.matches(0x1, expected = bytes(0x1)) {
            steps {
                advance(1)
                advance(0)
            }
        }
        parser.matches(0x1, 0x1, expected = bytes(0x1, 0x1)) {
            steps {
                advance(1)
                advance(0)
                advance(1)
            }
        }
        parser.matches(0x1, 0x1, 0x2, expected = bytes(0x1, 0x1, 0x2)) {
            steps {
                advance(1)
                advance(0)
                advance(2)
            }
        }

        // matches not predicate
        parser.doesNotMatch(0x1, 0x2) {
            expectEndOfInput()
            expect("not x01")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x2, 0x2) {
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
    fun `matches not one of byte followed by zero or more bytes`() {
        val parser = sequence(
            not(oneOf(0x1, 0x2)),
            zeroOrMore(oneOf(0x1, 0x2, 0x3, 0x4))
        )

        parser.expecting {
            expectSequence {
                expectNot {
                    expectOneOf(0x1, 0x2, hasResult = false)
                }
                expectZeroOrMoreSingleInput {
                    expectOneOf(0x1, 0x2, 0x3, 0x4)
                }
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

    @Test
    fun `matches not end of input followed by zero or more bytes`() {
        val parser = sequence(
            not(endOfInput(1)),
            zeroOrMore(oneOf(0x1, 0x2))
        )

        parser.expecting {
            expectSequence {
                expectNot {
                    expectEndOfInput()
                }
                expectZeroOrMoreSingleInput {
                    expectOneOf(0x1, 0x2)
                }
            }
        }

        parser.matches(0x2, expected = bytes(0x2))
        parser.matches(0x2, 0x1, expected = bytes(0x2, 0x1))

        // matches not predicate
        parser.doesNotMatch {
            expect("end of input")
            expect("not end of input")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x5) {
            expect("end of input")
            expect("not end of input")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x2, 0x5) {
            failAt(1)
            expect("end of input")
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }

    @Test
    fun `matches not of choices with common prefix followed by one or more chars`() {
        val parser = sequence(
            not(
                oneOf(
                    literal("a!"),
                    literal("a?")
                )
            ),
            oneOrMore(one())
        )

        parser.expecting {
            expectSequence {
                expectNot {
                    expectChoice {
                        expectLiteral("a!")
                        expectLiteral("a?")
                    }
                }
                expectOneOrMoreSingleInput {
                    expectOneChar()
                }
            }
        }

        parser.matches("ab", expected = listOf('a', 'b'))
        parser.matches("a", expected = listOf('a'))

        parser.doesNotMatch("a!") {
            expect("not \"a!\"")
            expect("not \"a?\"")
            expectOneChar()
        }
        parser.doesNotMatch("a?") {
            expect("not \"a!\"")
            expect("not \"a?\"")
            expectOneChar()
        }

        parser.doesNotMatch("a!X") {
            expect("not \"a!\"")
            expect("not \"a?\"")
            expectOneChar()
        }
    }

    @Test
    fun `matches not of decide followed by one or more chars`() {
        val parser = sequence(
            not(
                decide(optional(literal("a!", 1))) {
                    if (it == null) literal("0") else literal(it.toString())
                }
            ),
            oneOrMore(one())
        )

        parser.matches("X", expected = listOf('X'))
        parser.matches("a!", expected = listOf('a', '!'))
        parser.matches("a!2", expected = listOf('a', '!', '2'))

        parser.doesNotMatch("a!1") {
            expect("not \"a!\"")
            expect("not \"0\"")
            expectOneChar()
        }
        parser.doesNotMatch("0") {
            expect("not \"a!\"")
            expect("not \"0\"")
            expectOneChar()
        }

        parser.doesNotMatch("a!1X") {
            expect("not \"a!\"")
            expect("not \"0\"")
            expectOneChar()
        }
    }
}