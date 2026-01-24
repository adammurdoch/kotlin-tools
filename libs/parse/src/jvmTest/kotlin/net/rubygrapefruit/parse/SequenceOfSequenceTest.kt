package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class SequenceOfSequenceTest : AbstractParseTest() {
    @Test
    fun `matches sequence of literal then sequence of literal`() {
        val parser = sequence(
            sequence(literal("a", 1), literal("b", 2)) { a, b -> listOf(a, b) },
            sequence(literal("c", 3), literal("d", 4)) { a, b -> listOf(a, b) }
        ) { a, b -> a + b }

        parser.expecting {
            expectSequence {
                expectSequence {
                    expectLiteral("a", result = 1)
                    expectLiteral("b", result = 2)
                }
                expectSequence {
                    expectLiteral("c", result = 3)
                    expectLiteral("d", result = 4)
                }
            }
        }

        parser.matches("abcd", expected = listOf(1, 2, 3, 4))

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
            expectLiteral("c")
        }
        parser.doesNotMatch("abc") {
            failAt(3)
            expectLiteral("d")
        }

        // extra
        parser.doesNotMatch("abcdX") {
            failAt(4)
            expectEndOfInput()
        }
    }
}