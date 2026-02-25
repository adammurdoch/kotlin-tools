package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class OneOrMoreWithSeparatorTest : AbstractParseTest() {
    @Test
    fun `matches one or more char literals with separator`() {
        val parser = oneOrMore(literal("ab", 1), separator = literal(",", 2))

        parser.expecting {
            expectOneOrMore {
                expectLiteral("ab", result = 1)
                expectLiteral(",")
            }
        }

        parser.matches("ab", expected = listOf(1))
        parser.matches("ab,ab,ab", expected = listOf(1, 1, 1))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("a") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("ab,") {
            failAt(3)
            expectLiteral("ab")
        }
        parser.doesNotMatch("ab,a") {
            failAt(3)
            expectLiteral("ab")
        }

        // unexpected
        parser.doesNotMatch("aX") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectLiteral(",")
            expectEndOfInput()
        }
        parser.doesNotMatch("ab,X") {
            failAt(3)
            expectLiteral("ab")
        }
    }
}