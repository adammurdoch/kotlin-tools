package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardFirstSequenceTest : AbstractParseTest() {
    @Test
    fun `matches literal then literal`() {
        val parser = sequence(literal("a"), literal("b", 2))

        parser.expecting {
            expectSequence {
                expectLiteral("a")
                expectLiteral("b", result = 2)
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