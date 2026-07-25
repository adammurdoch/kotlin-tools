package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class OneOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches one or more text literals`() {
        val parser = oneOrMore(literal("a.", 1))

        parser.expecting {
            expectOneOrMore {
                expectLiteral("a.", result = 1)
            }
        }

        parser.matches("a.", expected = listOf(1)) {
            steps {
                advance(2)
                advance(0) // missing branch succeeds
            }
        }
        parser.matches("a.a.a.", expected = listOf(1, 1, 1)) {
            steps {
                advance(2)
                advance(0) // missing branch succeeds
                advance(1)
                advance(1)
                advance(0) // missing branch succeeds
                advance(1)
                advance(1)
                advance(0) // missing branch succeeds
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a.")
            steps {}
        }
        parser.doesNotMatch("a") {
            expectLiteral("a.")
            steps {}
        }
        parser.doesNotMatch("a.a") {
            failAt(2)
            expectLiteral("a.")
            expectEndOfInput()
            steps {
                advance(2)
                advance(0) // missing branch succeeds
            }
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a.")
            steps {}
        }
        parser.doesNotMatch("aX") {
            expectLiteral("a.")
            steps {}
        }
        parser.doesNotMatch("a.X") {
            failAt(2)
            expectLiteral("a.")
            expectEndOfInput()
            steps {
                advance(2)
                advance(0) // missing branch succeeds
            }
        }
    }
}
