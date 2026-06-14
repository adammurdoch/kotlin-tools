package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches zero or more text literals`() {
        val parser = zeroOrMore(literal("abc", 1))

        parser.expecting {
            expectZeroOrMore {
                expectLiteral("abc", result = 1)
            }
        }

        parser.matches("", expected = emptyList()) {
            steps {
                advance(0) // missing branch succeeds
            }
        }
        parser.matches("abc", expected = listOf(1)) {
            steps {
                advance(0) // missing branch succeeds
                advance(1)
                advance(2)
                advance(0) // missing branch succeeds
            }
        }
        parser.matches("abcabc", expected = listOf(1, 1)) {
            steps {
                advance(0) // missing branch succeeds
                advance(1)
                advance(2)
                advance(0) // missing branch succeeds
                advance(1)
                advance(2)
                advance(0) // missing branch succeeds
            }
        }

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0) // missing branch succeeds
            }
        }
        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0) // missing branch succeeds
                advance(1)
            }
        }
        parser.doesNotMatch("abca") {
            failAt(3)
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0) // missing branch succeeds
                advance(1)
                advance(2)
                advance(0) // missing branch succeeds
            }
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0) // missing branch succeeds
            }
        }
        parser.doesNotMatch("aX") {
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0) // missing branch succeeds
                advance(1)
            }
        }
        parser.doesNotMatch("abcaX") {
            failAt(3)
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0) // missing branch succeeds
                advance(1)
                advance(2)
                advance(0) // missing branch succeeds
                advance(1)
            }
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0) // missing branch succeeds
                advance(1)
                advance(2)
                advance(0) // missing branch succeeds
            }
        }
    }
}