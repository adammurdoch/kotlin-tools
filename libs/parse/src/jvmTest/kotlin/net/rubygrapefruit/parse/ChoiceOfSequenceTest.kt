package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class ChoiceOfSequenceTest : AbstractParseTest() {
    @Test
    fun `matches choice of sequences with common prefix`() {
        val parser = oneOf(
            sequence(literal("ab", 1), literal("c", 2)) { a, b -> listOf(a, b) },
            sequence(literal("a", 5), literal("b", 6)) { a, b -> listOf(a, b) }
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectLiteral("ab", result = 1)
                    expectLiteral("c", result = 2)
                }
                expectSequence {
                    expectLiteral("a", result = 5)
                    expectLiteral("b", result = 6)
                }
            }
        }

        parser.matches("abc", expected = listOf(1, 2))
        parser.matches("ab", expected = listOf(5, 6))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("ab")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectLiteral("c")
            expectEndOfInput()
        }
    }
}