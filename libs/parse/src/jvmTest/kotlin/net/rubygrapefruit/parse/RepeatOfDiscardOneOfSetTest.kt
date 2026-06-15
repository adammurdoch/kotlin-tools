package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class RepeatOfDiscardOneOfSetTest : AbstractParseTest() {
    @Test
    fun `matches n of one of char`() {
        val parser = repeat(
            3,
            discard(
                oneOf('a', 'b')
            )
        )

        parser.expecting {
            expectRepeatSingleInput(3, hasResult = false) {
                expectOneOf("a", "b")
            }
        }

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
        parser.doesNotMatch("ab") {
            failAt(2)
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
            steps {}
        }

        // extra
        parser.doesNotMatch("aaaX") {
            failAt(3)
            expectEndOfInput()
            steps {
                advance(3)
            }
        }
    }
}