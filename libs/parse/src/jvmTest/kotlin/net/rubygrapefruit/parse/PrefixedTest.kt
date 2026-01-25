package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.prefixed
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class PrefixedTest : AbstractParseTest() {
    @Test
    fun `matches literal then literal`() {
        val parser = prefixed(literal("a", 1), literal("b", 2))

        parser.expecting {
            expectSequence {
                expectLiteral("a")
                expectLiteral("a", result = 2)
            }
        }

        parser.matches("ab", expected = 2)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }
}