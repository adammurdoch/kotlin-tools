package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class SequenceOfChoiceTest : AbstractParseTest() {
    @Test
    fun `matches sequence of choices with no common prefix`() {
        val parser = sequence(
            oneOf(
                literal("ab", 1),
                literal("c", 2)
            ),
            oneOf(
                literal("11", 1),
                literal("2", 2)
            )
        ) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectChoice {
                    expectLiteral("ab", result = 1)
                    expectLiteral("c", result = 2)
                }
                expectChoice {
                    expectLiteral("11", result = 1)
                    expectLiteral("2", result = 2)
                }
            }
        }

        parser.matches("ab11", expected = listOf(1, 1))
        parser.matches("c2", expected = listOf(2, 2))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
            expectLiteral("c")
        }
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectLiteral("c")
        }
        parser.doesNotMatch("ab1") {
            failAt(2)
            expectLiteral("11")
            expectLiteral("2")
        }

        // unexpected
        parser.doesNotMatch("aX") {
            expectLiteral("ab")
            expectLiteral("c")
        }
        parser.doesNotMatch("c1X") {
            failAt(1)
            expectLiteral("11")
            expectLiteral("2")
        }
    }

    @Test
    fun `matches sequence of choice then literal when options have common prefix`() {
        val parser = sequence(
            oneOf(
                literal("abc", 1),
                literal("ad", 2)
            ),
            literal("12", 3)
        ) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectChoice {
                    expectLiteral("abc", result = 1)
                    expectLiteral("ad", result = 2)
                }
                expectLiteral("12", result = 3)
            }
        }

        parser.matches("abc12", expected = listOf(1, 3))
        parser.matches("ad12", expected = listOf(2, 3))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("ad1") {
            failAt(2)
            expectLiteral("12")
        }
    }

    @Test
    fun `matches sequence of choice then literal when one option is a prefix of another`() {
        val parser = sequence(
            oneOf(
                literal("abc", 1),
                literal("ab", 2)
            ),
            literal("12", 3)
        ) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectChoice {
                    expectLiteral("abc", result = 1)
                    expectLiteral("ab", result = 2)
                }
                expectLiteral("12", result = 3)
            }
        }

        parser.matches("abc12", expected = listOf(1, 3))
        parser.matches("ab12", expected = listOf(2, 3))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
            expectLiteral("abc")
        }
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectLiteral("abc")
        }
        parser.doesNotMatch("ab1") {
            failAt(2)
            expectLiteral("12")
        }

        // unexpected
        parser.doesNotMatch("aX") {
            expectLiteral("ab")
            expectLiteral("abc")
        }
    }

    @Test
    fun `matches sequence of same choice parser`() {
        val choice = oneOf(
            literal("abc", 1),
            literal("ad", 2)
        )
        val parser = sequence(
            choice,
            choice
        ) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectChoice {
                    expectLiteral("abc", result = 1)
                    expectLiteral("ad", result = 2)
                }
                expectChoice {
                    expectLiteral("abc", result = 1)
                    expectLiteral("ad", result = 2)
                }
            }
        }

        parser.matches("abcad", expected = listOf(1, 2))
        parser.matches("adabc", expected = listOf(2, 1))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("ad1") {
            failAt(2)
            expectLiteral("abc")
            expectLiteral("ad")
        }
    }
}