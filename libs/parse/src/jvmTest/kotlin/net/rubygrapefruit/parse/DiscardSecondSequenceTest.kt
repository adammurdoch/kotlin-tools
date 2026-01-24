package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardSecondSequenceTest : AbstractParseTest() {
    @Test
    fun `matches literal then literal`() {
        val parser = sequence(literal("a", 1), literal("b"))

        parser.expecting {
            expectSequence {
                expectLiteral("a", 1)
                expectLiteral("b")
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