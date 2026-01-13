package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
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
            expectLiteral("ab")
            expectLiteral("c")
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
    fun `matches sequence of choice then literal when choices have common prefix`() {
        val parser = sequence(
            oneOf(
                literal("abc", 1),
                literal("ad", 2)
            ),
            literal("12", 3)
        ) { a, b -> listOf(a, b) }

        parser.expecting {
            expectLiteral("abc")
            expectLiteral("ad")
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
            expectLiteral("abc")
            expectLiteral("ad")
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