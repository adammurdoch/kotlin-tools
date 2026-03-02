package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class Sequence5Test : AbstractParseTest() {
    @Test
    fun `matches sequence of char literals`() {
        val parser = sequence(
            literal("a", 1),
            literal("b", 2),
            literal("c", 3),
            literal("d", 4),
            literal("e", 5)
        ) { a, b, c, d, e -> listOf(a, b, c, d, e) }

        parser.expecting {
            expectSequence {
                expectLiteral("a", result = 1)
                expectSequence {
                    expectLiteral("b", result = 2)
                    expectSequence {
                        expectLiteral("c", result = 3)
                        expectSequence {
                            expectLiteral("d", result = 4)
                            expectLiteral("e", result = 5)
                        }
                    }
                }
            }
        }

        parser.matches("abcde", expected = listOf(1, 2, 3, 4, 5))

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
        parser.doesNotMatch("abcd") {
            failAt(4)
            expectLiteral("e")
        }

        // unexpected
        parser.doesNotMatch("abXde") {
            failAt(2)
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("abcdeX") {
            failAt(5)
            expectEndOfInput()
        }
    }
}