package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.suffixed
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class SuffixedTest : AbstractParseTest() {
    @Test
    fun `matches literal then literal`() {
        val parser = suffixed(literal("a", 1), literal("b", 2))

        parser.expecting {
            expectSequence {
                expectLiteral("a", result = 1)
                expectLiteral("a", result = 2)
            }
        }

        parser.matches("ab", expected = 1)

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