package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.oneOrMore
import kotlin.test.Test

class ChoiceOfOneOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches choice of one or more one of literal with common prefix`() {
        val parser = oneOf(
            oneOrMore(literal(byteArrayOf(0x1, 0x2), 1)),
            oneOrMore(literal(byteArrayOf(0x1, 0x3), 2)),
        )

        parser.expecting {
            expectChoice {
                expectOneOrMore {
                    expectLiteral(0x1, 0x2, result = 1)
                }
                expectOneOrMore {
                    expectLiteral(0x1, 0x3, result = 2)
                }
            }
        }

        parser.matches(0x1, 0x2, expected = listOf(1))
        parser.matches(0x1, 0x2, 0x1, 0x2, 0x1, 0x2, expected = listOf(1, 1, 1))
        parser.matches(0x1, 0x3, expected = listOf(2))
        parser.matches(0x1, 0x3, 0x1, 0x3, 0x1, 0x3, expected = listOf(2, 2, 2))

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
            expectLiteral(0x3)
        }
        parser.doesNotMatch(0x1, 0x2, 0x1) {
            failAt(3)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x1, 0) {
            failAt(1)
            expectLiteral(0x2)
            expectLiteral(0x3)
        }
        parser.doesNotMatch(0x1, 0x2, 0) {
            failAt(2)
            expectLiteral(0x1)
            expectEndOfInput()
        }
    }
}