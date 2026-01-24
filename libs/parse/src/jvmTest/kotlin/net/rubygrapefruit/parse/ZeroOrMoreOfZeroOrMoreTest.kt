package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
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
            expectChoice {
                expectOneOrMore {
                    expectZeroOrMoreSingleInput(0x1, 0x2)
                }
                expectZero()
            }
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

    @Test
    fun `matches zero or more of zero or more char literal`() {
        val parser = zeroOrMore(
            zeroOrMore(
                literal("12", 1)
            )
        )

        parser.expecting {
            emptyMatch()
            expectChoice {
                expectOneOrMore {
                    expectChoice {
                        expectOneOrMore {
                            expectLiteral("12", result = 1)
                        }
                        expectZero()
                    }
                }
                expectZero()
            }
        }

        parser.matches("", expected = listOf(emptyList()))
        parser.matches("12", expected = listOf(listOf(1), emptyList()))
        parser.matches("121212", expected = listOf(listOf(1, 1, 1), emptyList()))

        // unexpected
        parser.doesNotMatch("3") {
            expectLiteral("12")
            expectEndOfInput()
        }
        parser.doesNotMatch("13") {
            expectLiteral("12")
            expectEndOfInput()
        }
        parser.doesNotMatch("123") {
            failAt(2)
            expectLiteral("12")
            expectEndOfInput()
        }
    }
}