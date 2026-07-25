package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class OneOrMoreOfDiscardOneOfSetTest : AbstractParseTest() {
    @Test
    fun `matches one or more of one of char`() {
        val parser = oneOrMore(
            discard(
                oneOf('a', 'b')
            )
        )

        parser.expecting {
            expectOneOrMoreSingleInput(hasResult = false) {
                expectOneOf("a", "b")
            }
        }

        parser.matches("a") {
            steps {
                advance(1)
            }
        }
        parser.matches("b")
        parser.matches("baa") {
            steps {
                advance(3)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("b")
            steps {}
        }

        // unexpected
        parser.doesNotMatch("1") {
            expectLiteral("a")
            expectLiteral("b")
            steps {}
        }
        parser.doesNotMatch("ba1") {
            failAt(2)
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
            steps {
                advance(2)
            }
        }
    }
}