package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ZeroOrMoreProduceNothingTest : AbstractParseTest() {
    @Test
    fun `matches zero or more char literals`() {
        val parser = zeroOrMore(literal("ab"))

        parser.expecting {
            expectChoice {
                expectZeroOrMore(hasResult = false) {
                    expectLiteral("ab")
                }
                expectZero()
            }
        }

        parser.matches("")
        parser.matches("ab")
        parser.matches("abab")

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectEndOfInput()
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("ab")
            expectEndOfInput()
        }
        parser.doesNotMatch("aX") {
            expectLiteral("ab")
            expectEndOfInput()
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectLiteral("ab")
            expectEndOfInput()
        }
    }
}