package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.combinators.replace
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.oneOf
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
    fun `matches sequence of choice then literal when middle option is a prefix of another`() {
        val parser = sequence(
            oneOf(
                literal("abc", 1),
                literal("ab", 2),
                replace(repeat(2, oneOf('a', 'b')), 3)
            ),
            literal("123", 4)
        ) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectChoice {
                    expectLiteral("abc", result = 1)
                    expectLiteral("ab", result = 2)
                    expectMap {
                        expectRepeatSingleInput(2, hasResult = false) {
                            expectOneOf('a', 'b')
                        }
                    }
                }
                expectLiteral("123", result = 4)
            }
        }

        parser.matches("abc123", expected = listOf(1, 4)) {
            steps {
                advance(1)
                advance(1)
                advance(1)
                advance(3)
            }
        }
        parser.matches("ab123", expected = listOf(2, 4)) {
            steps {
                advance(1)
                advance(1)
                advance(0)
                advance(3)
            }
        }
        parser.matches("aa123", expected = listOf(3, 4)) {
            steps {
                advance(1)
                advance(1)
                advance(3)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
            expectLiteral("ab")
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("abc") {
            failAt(3)
            expectLiteral("123")
        }
        parser.doesNotMatch("ab") {
            failAt(2)
            expectLiteral("123")
        }
        parser.doesNotMatch("aa") {
            failAt(2)
            expectLiteral("123")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectLiteral("ab")
            expectLiteral("a")
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("ab123X") {
            failAt(5)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches sequence of choice parser`() {
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