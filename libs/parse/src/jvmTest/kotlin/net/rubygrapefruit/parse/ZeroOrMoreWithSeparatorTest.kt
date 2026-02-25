package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ZeroOrMoreWithSeparatorTest : AbstractParseTest() {
    @Test
    fun `matches zero or more char literals with separator`() {
        val parser = zeroOrMore(literal("abc", 1), separator = literal(",", 2))

        parser.expecting {
            expectZeroOrMore {
                expectLiteral("abc", result = 1)
                expectLiteral(",")
            }
        }

        parser.matches("", expected = emptyList())
        parser.matches("abc", expected = listOf(1))
        parser.matches("abc,abc,abc", expected = listOf(1, 1, 1))

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectEndOfInput()
        }
        parser.doesNotMatch("abc,") {
            failAt(4)
            expectLiteral("abc")
        }
        parser.doesNotMatch("abc,a") {
            failAt(4)
            expectLiteral("abc")
        }

        // unexpected
        parser.doesNotMatch("abc,X") {
            failAt(4)
            expectLiteral("abc")
        }

        // extra
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectEndOfInput()
        }
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectLiteral(",")
            expectEndOfInput()
        }
    }
}