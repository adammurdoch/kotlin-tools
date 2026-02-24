package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ZeroOrMoreOfChoiceTest : AbstractParseTest() {
    @Test
    fun `matches zero or more choice where one option is prefix of another`() {
        val parser = zeroOrMore(
            oneOf(
                literal("ab", 1),
                literal("a", 2)
            )
        )

        parser.expecting {
            expectChoice {
                expectZeroOrMore {
                    expectChoice {
                        expectLiteral("ab", result = 1)
                        expectLiteral("a", result = 2)
                    }
                }
                expectZero()
            }
        }

        parser.matches("", expected = emptyList())
        parser.matches("a", expected = listOf(2))
        parser.matches("ab", expected = listOf(1))
        parser.matches("aaab", expected = listOf(2, 2, 1))
        parser.matches("abaabab", expected = listOf(1, 2, 1, 1))

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
            expectLiteral("ab")
            expectEndOfInput()
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expectLiteral("a")
            expectLiteral("ab")
            expectEndOfInput()
        }
    }
}