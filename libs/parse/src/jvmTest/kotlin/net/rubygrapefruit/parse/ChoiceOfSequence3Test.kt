package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test
import kotlin.test.fail

class ChoiceOfSequence3Test : AbstractParseTest() {
    @Test
    fun `matches choice of sequence with common prefix`() {
        val parser = oneOf(
            sequence(literal("a", 1), literal("b", 2), literal("?", 3)) { a, b, c -> a + b + c },
            sequence(literal("a", 4), literal("b", 5), literal("!", 6)) { a, b, c -> a + b + c }
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectLiteral("a", 1)
                    expectSequence {
                        expectLiteral("b", 2)
                        expectLiteral("?", 3)
                    }
                }
                expectSequence {
                    expectLiteral("a", 4)
                    expectSequence {
                        expectLiteral("b", 5)
                        expectLiteral("!", 6)
                    }
                }
            }
        }

        parser.matches("ab?", 6)
        parser.matches("ab!", 15)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }
        parser.doesNotMatch("ab") {
            failAt(2)
            expectLiteral("?")
            expectLiteral("!")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectLiteral("?")
            expectLiteral("!")
        }
    }

    @Test
    fun `does not call map function of discarded option`() {
        val parser = oneOf(
            sequence(literal("ab", 1), literal("c", 2)) { a, b -> listOf(a, b) },
            sequence(literal("a", 5), literal("b", 6), literal("!", 7)) { _, _, _ -> fail() }
        )

        parser.matches("abc", expected = listOf(1, 2))
    }
}