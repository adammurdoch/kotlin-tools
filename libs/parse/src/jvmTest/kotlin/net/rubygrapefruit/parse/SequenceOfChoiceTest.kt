package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class SequenceOfChoiceTest : AbstractParseTest() {
    @Test
    fun `matches sequence of choices`() {
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
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectLiteral("c")
        }
        parser.doesNotMatch("ab1") {
            failAt(2)
            expectLiteral("11")
            expectLiteral("2")
        }
    }

    @Test
    fun `matches sequence of choice then literal when one choice is prefix of another`() {
        val parser = sequence(
            oneOf(
                literal("ab", 1),
                literal("a", 2)
            ),
            literal("bd", 3)
        ) { a, b -> "$a.$b" }

        parser.matches("abbd", expected = "1.3")
        parser.matches("abd", expected = "2.3")

        parser.doesNotMatch("aX") {
            failAt(1)
            expect("bd")
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expect("bd")
        }
    }
}