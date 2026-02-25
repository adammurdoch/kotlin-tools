package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ZeroOrMoreWithSeparatorProduceNothingTest : AbstractParseTest() {
    @Test
    fun `matches zero or more char literals with separator`() {
        val parser = zeroOrMore(literal("ab"), separator = literal(",", 1))

        parser.expecting {
            expectZeroOrMore(hasResult = false) {
                expectLiteral("ab")
                expectLiteral(",")
            }
        }

        parser.matches("")
        parser.matches("ab")
        parser.matches("ab,ab,ab")

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectEndOfInput()
        }
        parser.doesNotMatch("ab,") {
            failAt(3)
            expectLiteral("ab")
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
            expectLiteral(",")
            expectEndOfInput()
        }
    }
}