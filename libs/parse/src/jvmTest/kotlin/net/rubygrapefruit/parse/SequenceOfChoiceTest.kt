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
        ) { a, b -> "$a.$b" }

        parser.matches("ab11", expected = "1.1")
        parser.matches("c2", expected = "2.2")

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
        ) { a, b -> "$a.$b" }

        parser.matches("abc12", expected = "1.3")
        parser.matches("ad12", expected = "2.3")

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
}