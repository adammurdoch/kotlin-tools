package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class Sequence4Test : AbstractParseTest() {
    @Test
    fun `matches sequence of char literals`() {
        val parser = sequence(
            literal("a", 1),
            literal("b", 2),
            literal("c", 3),
            literal("d", 4)
        ) { a, b, c, d -> listOf(a, b, c, d) }

        parser.expecting {
            expectSequence {
                expectLiteral("a", result = 1)
                expectSequence {
                    expectLiteral("b", result = 2)
                    expectSequence {
                        expectLiteral("c", result = 3)
                        expectLiteral("d", result = 4)
                    }
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