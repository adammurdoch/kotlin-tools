package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test
import kotlin.test.fail

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

    @Test
    fun `matches choice of sequences of char match with common prefix then literal`() {
        val parser = oneOf(
            sequence(match(literal("a")), literal("b", "b")) { a, b -> a + b },
            sequence(match(literal("a")), literal("c", "c")) { a, b -> a + b },
        )

        parser.matches("ab", expected = "ab")
        parser.matches("ac", expected = "ac")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("acX") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches choice of sequences of zero or more of char with common prefix then literal`() {
        val parser = oneOf(
            sequence(zeroOrMore(oneOf('1', '2')), literal("b", 'b')) { a, b -> a + b },
            sequence(zeroOrMore(oneOf('1', '2')), literal("c", 'c')) { a, b -> a + b },
        )

        parser.matches("b", expected = listOf('b'))
        parser.matches("c", expected = listOf('c'))

        parser.matches("2b", expected = listOf('2', 'b'))
        parser.matches("1c", expected = listOf('1', 'c'))

        parser.matches("221b", expected = listOf('2', '2', '1', 'b'))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("1")
            expectLiteral("2")
            expectLiteral("b")
            expectLiteral("c")
        }
    }

    @Test
    fun `does not call map function of discarded option`() {
        val parser = oneOf(
            sequence(literal("ab", 1), literal("c", 2)) { a, b -> listOf(a, b) },
            sequence(literal("a", 5), literal("b", 6)) { _, _ -> fail() }
        )

        parser.matches("abc", expected = listOf(1, 2))
    }
}